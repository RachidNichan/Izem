const { initializeApp, cert } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');
const fs = require('fs');
const path = require('path');

const serviceAccount = require('./service-account.json');

initializeApp({
  credential: cert(serviceAccount)
});

const db = getFirestore();

const collections = ['categories', 'words', 'phrases', 'verbs', 'users'];

async function runBackup() {
  console.log('⏳ Starting Firestore backup...');

  const backupDir = path.join(__dirname, 'firestore_backup');
  if (!fs.existsSync(backupDir)){
      fs.mkdirSync(backupDir);
  }

  for (const col of collections) {
    try {
      console.log(`📥 Exporting collection: ${col}...`);
      const snapshot = await db.collection(col).get();
      const data = {};

      snapshot.forEach(doc => {
        data[doc.id] = doc.data();
      });

      const filePath = path.join(backupDir, `${col}.json`);
      fs.writeFileSync(filePath, JSON.stringify(data, null, 2), 'utf8');
      console.log(`✔ Saved to: firestore_backup/${col}.json`);
    } catch (error) {
      console.error(`❌ Error exporting ${col}:`, error);
    }
  }
  console.log('\n🎉 Backup complete! Check the "firestore_backup" folder on your laptop.');
}

runBackup();
