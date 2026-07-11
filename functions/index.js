const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");
admin.initializeApp();

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
