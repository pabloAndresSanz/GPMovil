package com.gylgroup.gpmovil;

/**
 * Created by gyl on 22/5/2017.
 */

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gylgroup.gpmovil.model.Agenda;
import com.gylgroup.gpmovil.model.Direccion;
import com.gylgroup.gpmovil.model.Medico;
import com.gylgroup.gpmovil.model.PeriodoTiempo;
import com.gylgroup.gpmovil.model.Turno;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.*;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gylgroup.gpmovil.R.layout.item;
import static com.gylgroup.gpmovil.SignInUtil.mCredential;

public class TurnosActivity extends BaseActivity
        implements EasyPermissions.PermissionCallbacks {
    // GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private TextView mTituloText;
    private LinearLayout turnoslv;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    private static final String BUTTON_TEXT = "Call Google Calendar API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private Medico medico;
    private Direccion direccion;

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos);
        setupActionBar();
        mTituloText = (TextView) findViewById(R.id.titulo_turnos);
        mOutputText = (TextView) findViewById(R.id.mensaje_turnos);
        turnoslv = (LinearLayout) findViewById(R.id.turnoslv);
        medico = (Medico) getIntent().getSerializableExtra("medico");
        direccion = (Direccion) getIntent().getSerializableExtra("direccion");
        mTituloText.setText(medico.getDescripcion());

        try {
            getResultsFromApi();
        } catch (Exception e) {
            mOutputText.setText(e.getMessage());
        }
    }


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                TurnosActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
    private abstract class MakeGoogleCalendarTask<T,U,V> extends AsyncTask<T,U,V> {
        protected com.google.api.services.calendar.Calendar mService = null;
        protected  Exception mLastError = null;
        MakeGoogleCalendarTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }
        @Override
        protected void onCancelled() {
            hideProgressDialog();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            TurnosActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }
    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends MakeGoogleCalendarTask<Void, Void, List<PeriodoTiempo>> {
        MakeRequestTask(GoogleAccountCredential credential) {
            super(credential);
        }

        @Override
        protected List<PeriodoTiempo> doInBackground(Void... params) {
            try {
                return getFreebusy();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<PeriodoTiempo> getFreebusy() throws IOException {
            List<PeriodoTiempo> frees = new ArrayList<>();
            try {
                // List the next 10 events from the primary calendar.

                java.util.Calendar cal = java.util.Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                cal = Calendar.getInstance();
                if(minute>=30) {
                    cal.set(year, month, day, hour + 1, 30);
                }
                else {
                    cal.set(year, month, day, hour+1, 0);
                }
                DateTime min = new DateTime(cal.getTimeInMillis());
                // cal.add(Calendar.MONTH, 2);
                cal.add(Calendar.DAY_OF_MONTH,7);
                DateTime max = new DateTime(cal.getTimeInMillis());
                List<FreeBusyRequestItem> items = new ArrayList<>();
                for(Agenda agenda:direccion.getAgendaCollection()) {
                    FreeBusyRequestItem item = new FreeBusyRequestItem();
                    item.setId(agenda.getAgenda());
                    items.add(item);
                }
                FreeBusyRequest req = new FreeBusyRequest();
                req.setTimeMin(min);
                req.setTimeMax(max);
                req.setItems(items);
                req.setTimeZone("America/Argentina/Buenos_Aires");
                FreeBusyResponse response = mService.freebusy().query(req).execute();
                for(String agenda:response.getCalendars().keySet()) {
                    FreeBusyCalendar freeBusyCalendar=response.getCalendars().get(agenda);
                    List<TimePeriod> busies = freeBusyCalendar.getBusy();
                    DateTime start = min;
                    for (TimePeriod busy : busies) {
                        if (busy.getStart().getValue() > start.getValue()) {
                            TimePeriod free = new TimePeriod();
                            free.setStart(start);
                            free.setEnd(busy.getStart());
                            addIntervalos(frees, free,agenda);
                        }
                        start = busy.getEnd();
                    }
                    if (start.getValue() < max.getValue()) {
                        TimePeriod free = new TimePeriod();
                        free.setStart(start);
                        free.setEnd(max);
                        addIntervalos(frees, free,agenda);
                    }
                    //turnoslv.setAdapter(new TimePeriodAdapter(TurnosActivity.this,frees));
                }
                return frees;
            } catch (Exception e) {
                throw (e);
            }
        }

        private void addIntervalos(List<PeriodoTiempo> list, TimePeriod item,String agenda) {
            DateTime start = item.getStart();
            DateTime end = new DateTime(start.getValue() + medico.getTurnoenmin() * 60000L);
            while (end.getValue() <= item.getEnd().getValue()) {
                TimePeriod timePeriod = new TimePeriod();
                timePeriod.setStart(start);
                timePeriod.setEnd(end);
                PeriodoTiempo periodoTiempo = new PeriodoTiempo();
                periodoTiempo.setTimePeriod(timePeriod);
                periodoTiempo.setAgenda(agenda);
                if (!list.contains(periodoTiempo)) {
                    list.add(periodoTiempo);
                }
                start = end;
                end = new DateTime(start.getValue() + medico.getTurnoenmin() * 60000L);
            }
        }

        @Override
        protected void onPostExecute(List<PeriodoTiempo> output) {
            int anterior = -1;
            mOutputText.setText("prueba");
            LinearLayout horasLayout = null;
            int cuantasHoras = 0;
            Collections.sort(output);
            for (PeriodoTiempo turno : output) {
                TextView fecha = new TextView(TurnosActivity.this);
                java.util.Calendar start = Calendar.getInstance();
                start.setTimeInMillis(turno.getTimePeriod().getStart().getValue());
                if (anterior != (int) start.get(Calendar.DAY_OF_YEAR)) {
                    anterior = (int) start.get(Calendar.DAY_OF_YEAR);
                    fecha.setText(String.format("%s %d de %s,",
                            start.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("es", "AR")),
                            start.get(Calendar.DAY_OF_MONTH),
                            start.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "AR"))
                    ));
                    fecha.setTextSize(20);
                    turnoslv.addView(fecha);
                    cuantasHoras=0;
                }
                if (cuantasHoras % 3 == 0) {
                    horasLayout = new LinearLayout(TurnosActivity.this);
                    horasLayout.setOrientation(LinearLayout.HORIZONTAL);
                    turnoslv.addView(horasLayout);
                }
                LinearLayout horaLayout = new LinearLayout(TurnosActivity.this);
                horaLayout.setGravity(Gravity.RIGHT);
                horaLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView hora = new TextView(TurnosActivity.this);
                hora.setText(String.format("%1$02d:%2$02d", start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE)));
                hora.setTextSize(20);
                hora.setGravity(Gravity.RIGHT);
                hora.setPadding(10, 0, 10, 10);
                horaLayout.addView(hora);
                ImageView horaImage = new ImageView(TurnosActivity.this);
                horaImage.setImageResource(R.drawable.agenda32);
                horaImage.setPadding(0, 0, 10, 10);
                horaLayout.addView(horaImage);
                horasLayout.addView(horaLayout);
                cuantasHoras++;
                horaLayout.setOnClickListener(new HorasClickListener(turno));
            }
            hideProgressDialog();
        }
    }

    private class MakeAddEventTask extends MakeGoogleCalendarTask<Void, Void, Void> {
        private DateTime start = null;
        private DateTime end = null;
        private String agenda=null;

        MakeAddEventTask(GoogleAccountCredential credential, PeriodoTiempo periodoTiempo) {
            super(credential);
            this.agenda=periodoTiempo.getAgenda();
            this.start = periodoTiempo.getTimePeriod().getStart();
            this.end = periodoTiempo.getTimePeriod().getEnd();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                return addEvent();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */

        private Void addEvent() throws IOException {
            try {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TurnosActivity.this);
                String titular = sharedPref.getString("titular", "");
                Event event = new Event()
                        .setSummary(String.format("%s tiene cita con %s", titular, medico.getNombre()))
                        .setLocation(direccion.getDescripcion())
                        .setDescription(String.format("%s tiene cita con %s \nCobertura: %s \nAfiliado: %s \nVigencia: %s", titular, medico.getNombre(),
                                DB.getCoberturaDescripcion(sharedPref.getString("cobertura", "0")),
                                sharedPref.getString("nrosocio", ""),
                                sharedPref.getString("vigencia", "")
                        ));

                //DateTime startDateTime = new DateTime("2017-05-25T09:00:00-03:00");
                EventDateTime start = new EventDateTime()
                        .setDateTime(this.start)
                        .setTimeZone("America/Argentina/Buenos_Aires");
                event.setStart(start);

                //DateTime endDateTime = new DateTime("2017-05-25T10:00:00-03:00");
                EventDateTime end = new EventDateTime()
                        .setDateTime(this.end)
                        .setTimeZone("America/Argentina/Buenos_Aires");
                event.setEnd(end);

                EventAttendee[] attendees = new EventAttendee[]{
                        new EventAttendee().setEmail(agenda)
                };
                event.setAttendees(Arrays.asList(attendees));
                List<EventReminder> reminderOverrides=new ArrayList<>();
                if (sharedPref.getBoolean("mailEnabled",false)) {
                    reminderOverrides.add(new EventReminder().setMethod("email").setMinutes(60 *
                            Integer.parseInt(sharedPref.getString("mailHours","24"))));
                }
                if (sharedPref.getBoolean("popupEnabled",false)) {
                    reminderOverrides.add(new EventReminder().setMethod("popup").setMinutes(
                            Integer.parseInt(sharedPref.getString("popupMinutes","10"))));
                }
                Event.Reminders reminders = new Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(reminderOverrides);
                event.setReminders(reminders);

                String calendarId = "primary";

                //Prueba para Gadget
                Event.Gadget gadget = new Event.Gadget();

                gadget.setTitle("Word of the Day");
                gadget.setType("application/x-google-gadgets+xml");
                gadget.setIconLink("https://www.thefreedictionary.com/favicon.ico");
                gadget.setLink("https://www.thefreedictionary.com/_/WoD/wod-module.xml");
                gadget.setDisplay("chip");
                gadget.setWidth(300);
                gadget.setHeight(136);

                Map<String, String> prefs = new HashMap<String,String>();
                prefs.put("Format", "0");
                prefs.put("Days", "1");
                gadget.setPreferences(prefs);


                event.setGadget(gadget);
                //Fin Prueba para Gadget

                event = mService.events().insert(calendarId, event).execute();
                addTurnoGPAdminWS(this.start);
                return null;
            } catch (Exception e) {
                throw (e);
            }
        }

        @Override
        protected void onPostExecute(Void output) {
            hideProgressDialog();
            finish();
        }
    }
    private void addTurnoGPAdminWS(DateTime start) {
        try {
            Turno turno = new Turno();
            turno.setIdmedico(medico);
            turno.setFechahora(new Date(start.getValue()));
            turno.setPaciente(SignInUtil.mCredential.getSelectedAccountName());
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();
            Retrofit restAdapter = new Retrofit.Builder()
                    .baseUrl(Utils.getWSURL(TurnosActivity.this))
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            GPAdimWS service = restAdapter.create(GPAdimWS.class);
            Call<Turno> createTurno = service.createTurno(turno);
            createTurno.enqueue(new Callback<Turno>() {
                @Override
                public void onResponse(Call<Turno> call, Response<Turno> response) {
                    if (response.isSuccessful()) {
                        Utils.error(TurnosActivity.this, "turno agregado a GPAdminWS");
                    } else {
                        // error response, no access to resource?
                        Utils.error(TurnosActivity.this, "GPAdminWS Respondio con error");
                    }
                }

                @Override
                public void onFailure(Call<Turno> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    Utils.error(TurnosActivity.this, "No se pudo conectar a GPAdminWS" + t.getMessage());
                }
            });
        }
        catch(Exception e) {
            Utils.error(TurnosActivity.this,e.getMessage());
        }
    }
    private class HorasClickListener implements View.OnClickListener {
        private PeriodoTiempo turno;

        public HorasClickListener(PeriodoTiempo turno) {
            this.turno = turno;
        }

        @Override
        public void onClick(View view) {
            java.util.Calendar start = Calendar.getInstance();
            start.setTimeInMillis(turno.getTimePeriod().getStart().getValue());
            new android.app.AlertDialog.Builder(TurnosActivity.this)
                    .setTitle("Nueva cita")
                    .setMessage(String.format("Realmente desea agendar una cita con \n%s \nen %s \npara el %s %d de %s a las %02d:%02d",
                            medico.getNombre(),
                            direccion.getDescripcion(),
                            start.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("es", "AR")),
                            start.get(Calendar.DAY_OF_MONTH),
                            start.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "AR")),
                            start.get(Calendar.HOUR_OF_DAY),
                            start.get(Calendar.MINUTE)
                    ))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            new MakeAddEventTask(mCredential, turno).execute();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();

        }
    }

}
