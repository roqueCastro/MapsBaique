package com.example.mapsbaique.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mapsbaique.R;
import com.example.mapsbaique.utilidades.Utilidades_Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Context context;
    private Marker marker;

    StringRequest stringRequest;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = MapsActivity.this;
        request = Volley.newRequestQueue(getApplicationContext());
        locationStart();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        gpsEnaDis();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        permisoGPS();
        mMap = googleMap;

        //MAXIMO Y MINIMO ZOOM DEL MAPA MARCADOR
       /* mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(15);*/

        // Add a marker in Sydney and move the camera
       /* LatLng ubicacion = new LatLng(1.9718144, -75.93246719999999);

        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Timana-Huila, Colombia").draggable(true));
        // MARCADOR CAMBIAR POSISION DE CAMARA
        CameraPosition camara = new CameraPosition.Builder()
                .target(ubicacion)
                .zoom(15)
                .bearing(90)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camara));*/
    }

    /*///COMIENZA PERMISOS GPS`*/

    private void permisoGPS() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            locationStart();
        } else {
            locationStart();
        }
    }


    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        gpsEnaDis();
        onstartGos(mlocManager,Local);
    }

    public class Localizacion implements LocationListener {
        MapsActivity mainActivity;

        /*public MainActivity getMainActivity() {
            return mainActivity;
        }*/

        public void setMainActivity(MapsActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {

           /*Toast.makeText(context, "Lat:" + loc.getLatitude() + "\n" +
                   "Long:" + loc.getLongitude(), Toast.LENGTH_SHORT).show();*/

            //      this.mainActivity.setLocation(loc);

           /* mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).draggable(true));*/


           LatLng ubicacion = new LatLng(loc.getLatitude(), loc.getLongitude());

          /* if (marker == null){
               marker = mMap.addMarker(new MarkerOptions().position(ubicacion).draggable(true));
           }else {
               marker.setPosition(new LatLng(loc.getLatitude(), loc.getLongitude()));
           }

            CameraPosition camara = new CameraPosition.Builder()
                    .target(ubicacion)
                    .zoom(15)
                    .bearing(90)
                    .tilt(30)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camara));*/

            String lat = String.valueOf(loc.getLatitude());
            String lng = String.valueOf(loc.getLongitude());
            cargarWebServiceRegistro(lat,lng);


        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            //Toast.makeText(getApplicationContext(), "Activo",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            gpsEnaDis();
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

    }

    private void gpsEnaDis() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            showAlertGPS("ACTIVAR", "GPS");
        }
    }

    private void onstartGos(LocationManager mlocManager, Localizacion Local) {
        gpsEnaDis();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, (LocationListener) Local);
    }

    private void showAlertGPS(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        builder.setPositiveButton("Activar gps", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /* TERMINA PERMISOS GPS */

    private void cargarWebServiceRegistro(final String latitude, final String longitude) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJSONRegistroEvento.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("Noregistra")){
                    Toast.makeText(getApplicationContext(), "No registro", Toast.LENGTH_SHORT).show();
                }else if(response.trim().equalsIgnoreCase("ErrorBaseDatos")){
                    Toast.makeText(getApplicationContext(), "Base de datos error", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "REGISTRO EXITOSO", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Tiempo caducado..", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("cx",latitude);
                paramentros.put("cy",longitude);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

}
