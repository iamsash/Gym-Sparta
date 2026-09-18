const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * Cloud Function HTTPS Callable: registrarAdministrador
 * Recibe: { nombres, apellidos, usuario, email, password, telefono, codigoAutorizacion }
 */
exports.registrarAdministrador = functions.https.onCall(async (data, context) => {
  const { nombres, apellidos, usuario, email, password, telefono, codigoAutorizacion } = data;

  // 1. Validaciones básicas de parámetros
  if (!email || !password || !nombres || !apellidos || !codigoAutorizacion) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "Todos los campos obligatorios deben ser proporcionados."
    );
  }

  const db = admin.firestore();

  // 2. Verificar código de autorización en el servidor (Sin exponer clave al cliente APK)
  const configDoc = await db.collection("configuracion").doc("sistema").get();

  if (configDoc.exists) {
    const claveMaestraValida = configDoc.data().codigoAutorizacionAdmin;
    if (claveMaestraValida && claveMaestraValida !== codigoAutorizacion) {
      throw new functions.https.HttpsError(
        "permission-denied",
        "Código de autorización del gimnasio no válido."
      );
    }
  } else {
    // Si la configuración inicial del sistema no existe aún en Firestore,
    // el primer código enviado se establece como la Clave Maestra Inicial
    await db.collection("configuracion").doc("sistema").set({
      codigoAutorizacionAdmin: codigoAutorizacion,
      nombreGimnasio: "Gym Sparta",
      fechaInicializacion: new Date().toISOString()
    });
  }

  try {
    // 3. Crear usuario en Firebase Authentication usando Firebase Admin SDK
    const userRecord = await admin.auth().createUser({
      email: email,
      password: password,
      displayName: `${nombres} ${apellidos}`.trim()
    });

    // 4. Asignar Custom Claim de Rol de Administrador ({ admin: true })
    await admin.auth().setCustomUserClaims(userRecord.uid, { admin: true });

    // 5. Guardar perfil de administrador en Firestore 'administradores/{uid}' (Sin contraseña)
    await db.collection("administradores").doc(userRecord.uid).set({
      nombres: nombres,
      apellidos: apellidos,
      usuario: usuario,
      email: email,
      telefono: telefono || "",
      estado: true,
      fechaRegistro: new Date().toISOString().split("T")[0]
    });

    return {
      success: true,
      uid: userRecord.uid,
      message: "Administrador registrado exitosamente con Custom Claims"
    };

  } catch (error) {
    console.error("Error al registrar administrador en Cloud Function:", error);
    throw new functions.https.HttpsError(
      "internal",
      error.message || "Error al procesar el registro de administrador."
    );
  }
});
