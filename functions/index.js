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
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        const interactionsSnapshot = await admin.firestore()
            .collection("interactions")
            .where("senderId", "==", senderId)
            .where("targetId", "==", targetId)
            .get();

        const recentInteractions = interactionsSnapshot.docs.filter(doc => {
            const timestamp = doc.data().timestamp;
            if (!timestamp) return false;
            const docDate = timestamp.toDate();
            return docDate >= oneHourAgo;
        });

        if (recentInteractions.length > 1) {
            console.log(`Notification blocked: Spam detected from ${senderId} to ${targetId}`);
            return null;
        }

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

        await admin.messaging().send(message);
        console.log(`Successfully sent (${userLanguage}) notification to user: ${targetId}`);

    } catch (error) {
        console.error("Error sending notification:", error);
    }
    return null;
});
