const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { onSchedule } = require("firebase-functions/v2/scheduler");
const admin = require("firebase-admin");
admin.initializeApp();

async function sendBatchNotifications(tokens, messagePayload) {
    if (!tokens || tokens.length === 0) return;

    const BATCH_SIZE = 500;
    const promises = [];

    for (let i = 0; i < tokens.length; i += BATCH_SIZE) {
        const chunk = tokens.slice(i, i + BATCH_SIZE);
        const payload = {
            ...messagePayload,
            tokens: chunk
        };
        promises.push(admin.messaging().sendEachForMulticast(payload));
    }

    await Promise.all(promises);
}

exports.onInteractionCreated = onDocumentCreated("interactions/{interactionId}", async (event) => {
    const snapshot = event.data;
    if (!snapshot) return null;

    const data = snapshot.data();
    if (!data) return null;

    const senderId = data.senderId;
    const senderName = data.senderName;
    const targetId = data.targetId;

    try {
        // 1. Spam Check: Only allow one notification per hour between the same two users
        const oneHourAgo = Date.now() - (60 * 60 * 1000);
        const interactionsSnapshot = await admin.firestore()
            .collection("interactions")
            .where("targetId", "==", targetId)
            .get();

        const recentCount = interactionsSnapshot.docs.filter(doc => {
            const d = doc.data();
            const ts = d.timestamp ? d.timestamp.toDate().getTime() : Date.now();
            return d.senderId === senderId && ts >= oneHourAgo;
        }).length;

        if (recentCount > 1) {
            console.log(`Spam check: Already sent a roar from ${senderId} to ${targetId} in the last hour. Skipping notification.`);
            return null;
        }

        // 2. Fetch Target User Data
        const userDoc = await admin.firestore().doc(`users/${targetId}`).get();
        if (!userDoc.exists) {
            console.log(`User ${targetId} not found.`);
            return null;
        }

        const userData = userDoc.data();
        const fcmToken = userData.fcmToken;
        const userLanguage = userData.language || "en";

        if (!fcmToken) {
            console.log(`User ${targetId} has no fcmToken.`);
            return null;
        }

        // 3. Prepare Notification
        let notificationTitle = "🦁 Challenge Roar!";
        let notificationBody = `${senderName} roared at you on the leaderboard! Take a quiz now to challenge them.`;

        if (userLanguage === "ar") {
            notificationTitle = "🦁 زئير تحدي!";
            notificationBody = `الأسد ${senderName} زأر في وجهك على لوحة الصدارة! أجب على اختبار الآن وتحدّاه.`;
        }

        const message = {
            token: fcmToken,
            notification: {
                title: notificationTitle,
                body: notificationBody
            },
            android: {
                priority: "high",
                notification: {
                    channelId: "izem_notifications",
                    sound: "default",
                    clickAction: "LEADERBOARD_ACTION"
                }
            },
            data: {
                type: "roar",
                senderName: senderName,
                navigate_to: "leaderboard",
                click_action: "LEADERBOARD_ACTION"
            }
        };

        // 4. Send Message
        await admin.messaging().send(message);
        console.log(`Successfully sent (${userLanguage}) notification to user: ${targetId}`);

    } catch (error) {
        console.error("Error in onInteractionCreated:", error);
    }
    return null;
});

/**
 * Replicates Java's SimpleDateFormat("yyyy-'W'ww", Locale.UK) in the Africa/Casablanca time zone.
 * Matches Android's weekly reset cycle (Monday to Sunday).
 */
function getCurrentWeek(date = new Date()) {
    const formatter = new Intl.DateTimeFormat("en-GB", {
        timeZone: "Africa/Casablanca",
        year: "numeric",
        month: "numeric",
        day: "numeric"
    });
    const parts = formatter.formatToParts(date);
    let year, month, day;
    for (const p of parts) {
        if (p.type === "year") year = parseInt(p.value, 10);
        if (p.type === "month") month = parseInt(p.value, 10) - 1;
        if (p.type === "day") day = parseInt(p.value, 10);
    }

    const d = new Date(Date.UTC(year, month, day));
    const dow = d.getUTCDay() === 0 ? 7 : d.getUTCDay(); // Mon=1..Sun=7

    // Find Thursday of the current week (ISO-8601 standard used by Locale.UK)
    const thursday = new Date(Date.UTC(year, d.getUTCMonth(), d.getUTCDate() - dow + 4));

    // Week 1 is the week containing the first Thursday of the year (contains Jan 4)
    const jan4 = new Date(Date.UTC(thursday.getUTCFullYear(), 0, 4));
    const jan4Dow = jan4.getUTCDay() === 0 ? 7 : jan4.getUTCDay();
    const firstThursday = new Date(Date.UTC(thursday.getUTCFullYear(), 0, 4 - jan4Dow + 4));

    const weekNum = 1 + Math.round((thursday - firstThursday) / (7 * 86400000));
    return `${year}-W${String(weekNum).padStart(2, "0")}`;
}

/**
 * Scheduled function to announce weekly leaderboard winners.
 * Runs every Sunday at 9:00 PM Africa/Casablanca.
 */
exports.weeklyLeaderboardAnnouncement = onSchedule({
    schedule: "0 21 * * 0",
    timeZone: "Africa/Casablanca",
}, async (event) => {
    const currentWeek = getCurrentWeek();

    try {
        const snapshot = await admin.firestore()
            .collection("users")
            .where("lastResetWeek", "==", currentWeek)
            .orderBy("weeklyXP", "desc")
            .limit(3)
            .get();

        if (snapshot.empty) {
            console.log(`No active users found for week ${currentWeek}`);
            return null;
        }

        const winners = snapshot.docs.map(doc => {
            const data = doc.data();
            return {
                name: data.displayName || "Izem",
                xp: data.weeklyXP || 0
            };
        });

        // Compose clean bilingual message
        let bodyEn = "Weekly winners:\n";
        let bodyAr = "أبطال الأسبوع:\n";

        winners.forEach((w, i) => {
            const rank = i + 1;
            bodyEn += `${rank}. ${w.name} (${w.xp} XP)\n`;
            bodyAr += `${rank}. ${w.name} (${w.xp} نقطة)\n`;
        });

        const allUsersSnapshot = await admin.firestore().collection("users").get();
        const arTokens = [];
        const enTokens = [];

        allUsersSnapshot.forEach(doc => {
            const uData = doc.data();
            if (uData.fcmToken) {
                if (uData.language === "ar") {
                    arTokens.push(uData.fcmToken);
                } else {
                    enTokens.push(uData.fcmToken);
                }
            }
        });

        if (arTokens.length > 0) {
            const arPayload = {
                notification: {
                    title: "🏆 أبطال الأسبوع في إيزم",
                    body: bodyAr.trim()
                },
                android: {
                    priority: "high",
                    notification: {
                        channelId: "izem_notifications",
                        clickAction: "LEADERBOARD_ACTION"
                    }
                },
                data: {
                    type: "announcement",
                    navigate_to: "leaderboard",
                    click_action: "LEADERBOARD_ACTION"
                }
            };
            await sendBatchNotifications(arTokens, arPayload);
            console.log(`Weekly Arabic announcement sent to ${arTokens.length} users.`);
        }

        if (enTokens.length > 0) {
            const enPayload = {
                notification: {
                    title: "🏆 Izem Weekly Champions",
                    body: bodyEn.trim()
                },
                android: {
                    priority: "high",
                    notification: {
                        channelId: "izem_notifications",
                        clickAction: "LEADERBOARD_ACTION"
                    }
                },
                data: {
                    type: "announcement",
                    navigate_to: "leaderboard",
                    click_action: "LEADERBOARD_ACTION"
                }
            };
            await sendBatchNotifications(enTokens, enPayload);
            console.log(`Weekly English announcement sent to ${enTokens.length} users.`);
        }

    } catch (error) {
        console.error("Error in weeklyLeaderboardAnnouncement:", error);
    }
    return null;
});