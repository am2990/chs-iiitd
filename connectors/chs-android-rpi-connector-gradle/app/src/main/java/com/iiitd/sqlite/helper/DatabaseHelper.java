package com.iiitd.sqlite.helper;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iiitd.networking.NetworkDevice;
import com.iiitd.networking.Sensor;
import com.iiitd.sqlite.model.Notification;
import com.iiitd.sqlite.model.Patient;
import com.iiitd.sqlite.model.PatientObservation;

public class DatabaseHelper extends SQLiteOpenHelper{

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ChsDb";

    // Table Names
    private static final String TABLE_PATIENT = "patient";
    private static final String TABLE_OBS = "obs";
    private static final String TABLE_SENSOR_OBS = "sensor_obs";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String TABLE_NETWORK_DEVICES = "network_devices";
    private static final String TABLE_SENSORS = "sensors";

    // Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_CREATED_AT = "created_at";

    // Patient Table - column nmaes
    private static final String KEY_UUID = "uuid";
    private static final String KEY_NAME = "name";
    private static final String KEY_DOB = "dob";
    private static final String KEY_GENDER = "gender";

    // OBS Table - column names
    private static final String KEY_TEMPERATURE = "temperature";
    private static final String KEY_ALLERGIES = "allergies";
    private static final String KEY_PATIENTID = "patient_id";


    // Notification Table Column Names
    private static final String KEY_NOTIFICATION = "notification";


    // NETWORK DEVICES Table - column names
    private static final String KEY_DEVICENAME = "device_name";
    private static final String KEY_IPADDRESS = "ip_address";
    private static final String KEY_MACADDRESS = "mac_address";
    private static final String KEY_SENSORLIST = "sensor_list";


    // Sensor Table - column names
    private static final String KEY_SENSORNAME = "sensor_name";
    private static final String KEY_SENSORTYPE = "sensor_type";
    private static final String KEY_SENSOR_READINGS = "sensor_reading";
    private static final String KEY_SENSOR_READING_COUNT = "sensor_reading_count";
    private static final String KEY_OBSERVATION_ID = "sensor_obs_id";
    private static final String KEY_PATIENT_ID = "sensor_patient_id";


    // Table Create Statements
    // PATIENT table create statement
    private static final String CREATE_TABLE_PATIENT = "CREATE TABLE "
            + TABLE_PATIENT + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_UUID
            + " TEXT," + KEY_NAME + " TEXT," + KEY_DOB + " TEXT," + KEY_GENDER + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    // OBS table create statement
    private static final String CREATE_TABLE_OBS = "CREATE TABLE " + TABLE_OBS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TEMPERATURE + " TEXT,"
            + KEY_PATIENTID + " TEXT," + KEY_ALLERGIES + " TEXT," +  KEY_CREATED_AT + " DATETIME" + ")";


    //TODO Add patient id to notification
    private static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE " + TABLE_NOTIFICATIONS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_OBSERVATION_ID + " INTEGER,"+ KEY_NOTIFICATION + " TEXT,"
            +  KEY_CREATED_AT + " DATETIME" + ")";


    private static final String CREATE_TABLE_NETWORK_DEVICES = "CREATE TABLE " + TABLE_NETWORK_DEVICES
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DEVICENAME + " TEXT," + KEY_IPADDRESS + " TEXT,"
            + KEY_MACADDRESS + " TEXT," + KEY_SENSORLIST + " TEXT," +   KEY_CREATED_AT + " DATETIME" + ")";

    private static final String CREATE_TABLE_SENSORS = "CREATE TABLE " + TABLE_SENSORS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SENSORNAME + " TEXT," + KEY_SENSORTYPE + " TEXT," +
            KEY_OBSERVATION_ID + " INTEGER," + KEY_SENSOR_READINGS + " TEXT," + KEY_PATIENT_ID + " INTEGER," +
            KEY_CREATED_AT + " DATETIME" + ")";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_PATIENT);
        db.execSQL(CREATE_TABLE_OBS);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS);
        db.execSQL(CREATE_TABLE_NETWORK_DEVICES);
        db.execSQL(CREATE_TABLE_SENSORS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NETWORK_DEVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);
        // create new tables
        onCreate(db);
    }

    /*
     * Adding a patient
     */
    public long createPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UUID, patient.getUUID());
        values.put(KEY_NAME, patient.getName());
        values.put(KEY_DOB, patient.getDob());
        values.put(KEY_GENDER, patient.getGender());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long patient_id = db.insert(TABLE_PATIENT, null, values);

        return patient_id;
    }


    /*
     * Adding a Network Device  
     */
    public long addConnectedDevice(NetworkDevice device) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        if(getDeviceByMac(device.getMacAddress())==null){

            ContentValues values = new ContentValues();
            values.put(KEY_DEVICENAME, device.getDeviceName());
            values.put(KEY_IPADDRESS, device.getIpAddress());
            values.put(KEY_MACADDRESS, device.getMacAddress());
            values.put(KEY_SENSORLIST, gson.toJson(device.getSensorList()));
            values.put(KEY_CREATED_AT, getDateTime());

            // insert row
            long device_id = db.insert(TABLE_NETWORK_DEVICES, null, values);
            return device_id;

        }

        return 0L;
    }

    public long removeConnectedDevice(NetworkDevice device) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = KEY_MACADDRESS +"= ? ";
        String[] whereArgs = {  device.getMacAddress() };

        return db.delete(TABLE_NETWORK_DEVICES, whereClause, whereArgs);

    }

    public List<NetworkDevice> getAllConnectedDevices() {
        List<NetworkDevice> devices = new ArrayList<NetworkDevice>();

        String selectQuery = "SELECT  * FROM " + TABLE_NETWORK_DEVICES;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Gson gson = new Gson();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                int id  = c.getInt((c.getColumnIndex(KEY_ID)));
                String name = c.getString(c.getColumnIndex(KEY_DEVICENAME));
                String ip = c.getString(c.getColumnIndex(KEY_IPADDRESS));
                String mac = c.getString(c.getColumnIndex(KEY_MACADDRESS));
                String sensor_json = c.getString(c.getColumnIndex(KEY_SENSORLIST));
                String created = c.getString(c.getColumnIndex(KEY_CREATED_AT));

                Type listType = new TypeToken<List<Sensor>>(){}.getType();
                List<Sensor> sensors = (List<Sensor>) gson.fromJson(sensor_json, listType);

                NetworkDevice n = new NetworkDevice();
                n.setDeviceName(name); n.setId(id); n.setIpAddress(ip); n.setMacAddress(mac); n.setSensorList(sensors);
                // adding to devices list
                devices.add(n);
            } while (c.moveToNext());
        }

        return devices;

    }

    public NetworkDevice getDeviceByMac(String macAddress){

        Gson gson = new Gson();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NETWORK_DEVICES + " WHERE "
                + KEY_MACADDRESS + " = " + "'" + macAddress + "'";
        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();
        if(c.getCount() == 0)
            return null;


        int id  = c.getInt((c.getColumnIndex(KEY_ID)));
        String name = c.getString(c.getColumnIndex(KEY_DEVICENAME));
        String ip = c.getString(c.getColumnIndex(KEY_IPADDRESS));
        String mac = c.getString(c.getColumnIndex(KEY_MACADDRESS));
        String sensor_json = c.getString(c.getColumnIndex(KEY_SENSORLIST));
        String created = c.getString(c.getColumnIndex(KEY_CREATED_AT));

        Type listType = new TypeToken<List<Sensor>>(){}.getType();
        List<Sensor> sensors = (List<Sensor>) gson.fromJson(sensor_json, listType);

        NetworkDevice n = new NetworkDevice();
        n.setDeviceName(name); n.setId(id); n.setIpAddress(ip); n.setMacAddress(mac); n.setSensorList(sensors);

        return n;

    }



    //** Sensor Table Functions

    /*
     * Adding a Sensor   
     */
    public long addSensor(Sensor sensor) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        if(sensor != null){

            ContentValues values = new ContentValues();
            values.put(KEY_SENSORNAME, sensor.getSensorName());
            values.put(KEY_SENSORTYPE, sensor.getSensorType().toString());
//            values.put(KEY_SENSOR_READING_COUNT, sensor.getReadingCount());
            values.put(KEY_OBSERVATION_ID, sensor.getObsId());
            values.put(KEY_SENSOR_READINGS, gson.toJson(sensor.getReadings()));
            values.put(KEY_PATIENT_ID, sensor.getPatientId());
            values.put(KEY_CREATED_AT, getDateTime());

            // insert row
            long sensor_id = db.insert(TABLE_SENSORS, null, values);
            return sensor_id;

        }

        return 0L;
    }


    public Sensor getSensorByName(String sensorName){

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SENSORS + " WHERE "
                + KEY_SENSORNAME + " = " + "'" + sensorName + "'";
        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();
        if(c.getCount() == 0)
            return null;


        int id  = c.getInt((c.getColumnIndex(KEY_ID)));
        String name = c.getString(c.getColumnIndex(KEY_SENSORNAME));
        int readingCount = c.getInt(c.getColumnIndex(KEY_SENSOR_READING_COUNT));
        String sensorType = c.getString(c.getColumnIndex(KEY_SENSORTYPE));
        String created = c.getString(c.getColumnIndex(KEY_CREATED_AT));

        Sensor sensor = new Sensor();
        sensor.setSensorName(name);
        sensor.setReadingCount(readingCount);
        sensor.setDatetime(created);
        sensor.setId(id);
        sensor.setSensorType(sensorType);

        return sensor;

    }

    public Sensor getSensorByObsId(int obs_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SENSORS + " WHERE "
                + KEY_OBSERVATION_ID + " = " +  obs_id ;
        Log.d(LOG, selectQuery);

        Gson gson = new Gson();

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();
        if(c.getCount() == 0)
            return null;


        int id  = c.getInt((c.getColumnIndex(KEY_ID)));
        String name = c.getString(c.getColumnIndex(KEY_SENSORNAME));
//        int readingCount = c.getInt(c.getColumnIndex(KEY_SENSOR_READING_COUNT));
        String readings = c.getString(c.getColumnIndex(KEY_SENSOR_READINGS));
        String sensorType = c.getString(c.getColumnIndex(KEY_SENSORTYPE));
        String created = c.getString(c.getColumnIndex(KEY_CREATED_AT));

        Type listType = new TypeToken<List<String>>(){}.getType();
        List<String> sensor_readings = (List<String>) gson.fromJson(readings, listType);

        Sensor sensor = new Sensor();
//        sensor.setReadingCount(readingCount);
        sensor.setSensorName(name);
        sensor.setDatetime(created);
        sensor.setId(id);
        sensor.setSensorType(sensorType);
        sensor.setReading(sensor_readings);

        return sensor;

    }

    /**
     *
     * @param notification
     * @return
     */

    public long addNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OBSERVATION_ID, notification.getObs_id());
        values.put(KEY_NOTIFICATION, notification.getNotification());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long notf_id = db.insert(TABLE_NOTIFICATIONS, null, values);

        return notf_id;
    }

    public Notification getNotificationByObsId(int obs_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTIFICATIONS + " WHERE "
                + KEY_OBSERVATION_ID + " = " +  obs_id ;
        Log.d(LOG, selectQuery);


        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();
        if(c.getCount() == 0)
            return null;


        int id  = c.getInt((c.getColumnIndex(KEY_ID)));
        int obsId = c.getInt(c.getColumnIndex(KEY_OBSERVATION_ID));
        String notes = c.getString(c.getColumnIndex(KEY_NOTIFICATION));
        String created = c.getString(c.getColumnIndex(KEY_CREATED_AT));

        Notification notification = new Notification();
        notification.set_id(id);
        notification.setNotification(notes);
        notification.setObs_id(obsId);
        notification.setCreated_at(created);

        return notification;

    }

    /**
     * getting all patients under single tag
     * */
    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NOTIFICATIONS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                int id  = c.getInt((c.getColumnIndex(KEY_ID)));
                int obsId = c.getInt(c.getColumnIndex(KEY_OBSERVATION_ID));
                String notes = c.getString(c.getColumnIndex(KEY_NOTIFICATION));
                String created = c.getString(c.getColumnIndex(KEY_CREATED_AT));

                Notification notification = new Notification();
                notification.set_id(id);
                notification.setNotification(notes);
                notification.setObs_id(obsId);
                notification.setCreated_at(created);

                // adding to patient list
                notifications.add(notification);
            } while (c.moveToNext());
        }

        return notifications;

    }


    /**
     * getting all patients under single tag
     * */
    public List<String> getAllPatients() {
        List<String> patients = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + TABLE_PATIENT;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Patient patient = new Patient();
                patient.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                patient.setUUID((c.getString(c.getColumnIndex(KEY_UUID))));
                patient.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                patient.setDob((c.getString(c.getColumnIndex(KEY_DOB))));
                patient.setGender((c.getString(c.getColumnIndex(KEY_GENDER))));
                patient.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to patient list
                patients.add(patient.toString());
            } while (c.moveToNext());
        }

        return patients;

    }

    /**
     * getting all patients under single tag
     * */
    public List<Patient> getAllPatientsObjects() {
        List<Patient> patients = new ArrayList<Patient>();

        String selectQuery = "SELECT  * FROM " + TABLE_PATIENT;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Patient patient = new Patient();
                patient.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                patient.setUUID((c.getString(c.getColumnIndex(KEY_UUID))));
                patient.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                patient.setDob((c.getString(c.getColumnIndex(KEY_DOB))));
                patient.setGender((c.getString(c.getColumnIndex(KEY_GENDER))));
                patient.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to patient list
                patients.add(patient);
            } while (c.moveToNext());
        }

        return patients;

    }

    public long addPatientObservation(PatientObservation obs) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATIENTID, obs.getPatientId());
        values.put(KEY_TEMPERATURE, obs.getTemperature());
        values.put(KEY_ALLERGIES, obs.getAllergies());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long obs_id = db.insert(TABLE_OBS, null, values);

        return obs_id;
    }

    public Patient getPatientById(int patient_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PATIENT + " WHERE "
                + KEY_ID + " = " + patient_id;
        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Patient patient = new Patient();
        patient.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        patient.setUUID(c.getString(c.getColumnIndex(KEY_UUID)));
        patient.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        patient.setGender(c.getString(c.getColumnIndex(KEY_GENDER)));
        patient.setDob(c.getString(c.getColumnIndex(KEY_DOB)));

        return patient;
    }

    public List<PatientObservation> getObsById(int patient_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_OBS + " WHERE "
                + KEY_PATIENTID + " = " + patient_id;
        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        List<PatientObservation> obs_list = new ArrayList<PatientObservation>();

        if (c.moveToFirst()) {
            do {
                PatientObservation p_obs = new PatientObservation();
                p_obs.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                p_obs.setPatientId(c.getInt(c.getColumnIndex(KEY_PATIENTID)));
                p_obs.setTemperature((c.getString(c.getColumnIndex(KEY_TEMPERATURE))));
                p_obs.setAllergies(c.getString(c.getColumnIndex(KEY_ALLERGIES)));
                p_obs.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                obs_list.add(p_obs);
            } while (c.moveToNext());
        }

        return obs_list;
    }



    public Sensor getSensorById(int id){

        //TODO implement get sensorby ID

        return null;
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // closing database
    public void closeDb() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
