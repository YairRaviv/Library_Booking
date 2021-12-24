package com.example.floorview;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

public class AsyncTasksWrapper {
    static class ExtractDataFromDbTask extends AsyncTask<Void, Void, HashMap<String, ReservableObject>> {
        LoadingBar loadingBar;
        Context context;
        FloorState floorState;
        AsyncTasksWrapper.ExtractDataFromDbTask.AsyncTaskListener listener;

        public ExtractDataFromDbTask(Context context, LoadingBar loadingBar, FloorState floorState) {
            this.context = context;
            this.loadingBar = loadingBar;
            this.floorState = floorState;
        }

        @Override
        protected HashMap<String, ReservableObject> doInBackground(Void... voids) {
            HashMap<String, ReservableObject> floorClassroomsState = floorState.getFloorState();
            return floorClassroomsState;
        }

        @Override
        protected void onPreExecute() {
            loadingBar.showDialog();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(HashMap<String, ReservableObject> result) {  // result is data returned by doInBackground
            loadingBar.dismiss();
            listener.onAsyncTaskFinished(result);
            super.onPostExecute(result);
        }
        public void setListener(AsyncTasksWrapper.ExtractDataFromDbTask.AsyncTaskListener listener) {
            this.listener = listener;
        }

        public interface AsyncTaskListener {
            void onAsyncTaskFinished(HashMap<String, ReservableObject> result);
        }
    }

    static class ExecuteClassroomsUpdateTask extends AsyncTask<Void, Void, Boolean> {
        LoadingBar loadingBar;
        Context context;
        ExecuteClassroomsUpdateTask.AsyncTaskListener listener;
        ArrayList<Reservation> selectedClassroomsReservations;
        String userId;
        FloorState floorState;

        public ExecuteClassroomsUpdateTask(Context context, LoadingBar loadingBar, FloorState floorState, ArrayList<Reservation> selectedClassroomsReservations, String userId)
        {
            this.context = context;
            this.loadingBar = loadingBar;
            this.floorState = floorState;
            this.selectedClassroomsReservations = selectedClassroomsReservations;
            this.userId = userId;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                floorState.addReservationsToDB(selectedClassroomsReservations, userId);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            loadingBar.showDialog();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);// result is data returned by doInBackground
            loadingBar.dismiss();
            listener.onAsyncTaskFinished(result);
        }
        public void setListener(ExecuteClassroomsUpdateTask.AsyncTaskListener listener) {
            this.listener = listener;
        }

        public interface AsyncTaskListener {
            void onAsyncTaskFinished(Boolean result);
        }
    }

    static class ExecuteTableUpdateTask extends AsyncTask<Void, Void, Boolean> {
        LoadingBar loadingBar;
        Context context;
        ExecuteTableUpdateTask.AsyncTaskListener listener;
        String clickedTableId;
        Time selectedEndTime;
        String userId;
        FloorState floorState;

        public ExecuteTableUpdateTask(Context context, LoadingBar loadingBar, FloorState floorState, String clickedTableId, Time selectedEndTime , String userId)
        {
            this.context = context;
            this.loadingBar = loadingBar;
            this.floorState = floorState;
            this.clickedTableId = clickedTableId;
            this.selectedEndTime = selectedEndTime;
            this.userId = userId;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                floorState.addReservationToDB(clickedTableId, selectedEndTime, userId);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            loadingBar.showDialog();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);// result is data returned by doInBackground
            loadingBar.dismiss();
            listener.onAsyncTaskFinished(result);
        }
        public void setListener(ExecuteTableUpdateTask.AsyncTaskListener listener) {
            this.listener = listener;
        }

        public interface AsyncTaskListener {
            void onAsyncTaskFinished(Boolean result);
        }
    }

    static class ExecuteQueryTask extends AsyncTask<String, Void, ResultSet> {
        LoadingBar loadingBar;
        String query;
        ExecuteQueryTask.AsyncTaskListener listener;


        public ExecuteQueryTask(AppCompatActivity context)
        {
            loadingBar = new LoadingBar(context);
            this.loadingBar = loadingBar;
        }


        @Override
        protected ResultSet doInBackground(String... strings) {
            ResultSet resultSet = null;
            for(String query : strings) {
                DBConnector dbConnector = DBConnector.getInstance();
                resultSet = dbConnector.executeQuery(query);
            }
            return resultSet;
        }

        @Override
        protected void onPreExecute() {
            loadingBar.showDialog();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);// result is data returned by doInBackground
            loadingBar.dismiss();
            listener.onAsyncTaskFinished(result);
        }
        public void setListener(ExecuteQueryTask.AsyncTaskListener listener) {
            this.listener = listener;
        }

        public interface AsyncTaskListener {
            void onAsyncTaskFinished(ResultSet result);
        }
    }
}
