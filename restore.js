const { initializeApp, cert } = require('firebase-admin/app');
const { getFirestore, Timestamp } = require('firebase-admin/firestore');
const fs = require('fs');
const path = require('path');

const serviceAccount = require('./service-account.json');

initializeApp({
  credential: cert(serviceAccount)
});

const db = getFirestore();

const collections = ['categories', 'words', 'phrases', 'verbs', 'users'];

function parseTimestamps(obj) {
  if (typeof obj !== 'object' || obj === null) return obj;

  for (const key in obj) {
    if (obj[key] && typeof obj[key] === 'object') {
      const subObj = obj[key];
      if (typeof subObj._seconds === 'number' && typeof subObj._nanoseconds === 'number') {
        obj[key] = new Timestamp(subObj._seconds, subObj._nanoseconds);
      } else {
        parseTimestamps(subObj);
      }
    }
  }
  return obj;
}

async function runRestore() {
  console.log('⏳ Starting Firestore database restore...');
  const backupDir = path.join(__dirname, 'firestore_backup');

  for (const col of collections) {
    try {
      const filePath = path.join(backupDir, `${col}.json`);
      if (!fs.existsSync(filePath)) {
        console.log(`⚠ Skipping: No backup file found for ${col}.json`);
        continue;
      }

      console.log(`📤 Importing collection: ${col}...`);
      const fileData = fs.readFileSync(filePath, 'utf8');
      const documents = JSON.parse(fileData);

      let count = 0;
      for (const [docId, docData] of Object.entries(documents)) {
        const parsedData = parseTimestamps(docData);

        await db.collection(col).doc(docId).set(parsedData);
        count++;
      }
      console.log(`✔ Restored ${count} documents successfully to collection: ${col}`);
    } catch (error) {
      console.error(`❌ Error restoring collection ${col}:`, error);
    }
  }
  console.log('\n🎉 Database restore complete! All your collections have been successfully re-uploaded.');
}

runRestore();
