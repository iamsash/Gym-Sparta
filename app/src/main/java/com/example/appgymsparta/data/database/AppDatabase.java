package com.example.appgymsparta.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.appgymsparta.data.converter.DateTimeConverter;
import com.example.appgymsparta.data.dao.AdministradorDao;
import com.example.appgymsparta.data.dao.AsistenciaDao;
import com.example.appgymsparta.data.dao.ClienteDao;
import com.example.appgymsparta.data.dao.DetalleRutinaDao;
import com.example.appgymsparta.data.dao.InscripcionDao;
import com.example.appgymsparta.data.dao.MembresiaDao;
import com.example.appgymsparta.data.dao.PagoDao;
import com.example.appgymsparta.data.dao.RutinaDao;
import com.example.appgymsparta.data.entity.Administrador;
import com.example.appgymsparta.data.entity.Asistencia;
import com.example.appgymsparta.data.entity.Cliente;
import com.example.appgymsparta.data.entity.DetalleRutina;
import com.example.appgymsparta.data.entity.Inscripcion;
import com.example.appgymsparta.data.entity.Membresia;
import com.example.appgymsparta.data.entity.Pago;
import com.example.appgymsparta.data.entity.Rutina;

@Database(
    entities = {
        Cliente.class,
        Membresia.class,
        Inscripcion.class,
        Pago.class,
        Asistencia.class,
        Rutina.class,
        DetalleRutina.class,
        Administrador.class
    },
    version = 6,
    exportSchema = false
)
@TypeConverters(DateTimeConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "app_gym_sparta.db";
    private static volatile AppDatabase instance;

    // Métodos abstractos para exponer los DAOs
    public abstract ClienteDao clienteDao();
    public abstract MembresiaDao membresiaDao();
    public abstract InscripcionDao inscripcionDao();
    public abstract PagoDao pagoDao();
    public abstract AsistenciaDao asistenciaDao();
    public abstract RutinaDao rutinaDao();
    public abstract DetalleRutinaDao detalleRutinaDao();
    public abstract AdministradorDao administradorDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `pagos_new` (" +
                    "`idPago` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`idInscripcion` INTEGER, " +
                    "`tipoPago` TEXT, " +
                    "`monto` REAL NOT NULL, " +
                    "`fechaPago` TEXT, " +
                    "`metodoPago` TEXT, " +
                    "`numeroOperacion` TEXT, " +
                    "`observacion` TEXT, " +
                    "FOREIGN KEY(`idInscripcion`) REFERENCES `inscripciones`(`idInscripcion`) ON UPDATE NO ACTION ON DELETE RESTRICT" +
                    ")");

            database.execSQL("INSERT INTO `pagos_new` (`idPago`, `idInscripcion`, `tipoPago`, `monto`, `fechaPago`, `metodoPago`, `numeroOperacion`, `observacion`) " +
                    "SELECT `idPago`, `idInscripcion`, 'Membresia', `monto`, `fechaPago`, `metodoPago`, `numeroOperacion`, `observacion` FROM `pagos`");

            database.execSQL("DROP TABLE `pagos`");

            database.execSQL("ALTER TABLE `pagos_new` RENAME TO `pagos`");

            database.execSQL("CREATE INDEX IF NOT EXISTS `index_pagos_idInscripcion` ON `pagos` (`idInscripcion`)");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `administradores` (" +
                    "`idAdministrador` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`nombres` TEXT, " +
                    "`apellidos` TEXT, " +
                    "`usuario` TEXT, " +
                    "`password` TEXT, " +
                    "`telefono` TEXT, " +
                    "`estado` INTEGER NOT NULL, " +
                    "`fechaRegistro` TEXT" +
                    ")");
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME
            )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}
