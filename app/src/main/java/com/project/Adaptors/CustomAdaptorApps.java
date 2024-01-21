package com.project.Adaptors;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.Helper.AppsModel;
import com.project.internship_task_1.ActivityLatestUpdate;
import com.project.internship_task_1.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class CustomAdaptorApps extends RecyclerView.Adapter<CustomAdaptorApps.viewHolder> implements Filterable {

    private String text_type;
    UninstallInterface uninstallInterface;
    private ArrayList<AppsModel> mDisplayedValues;    // Values to be displayed
    private ArrayList<AppsModel> apps_list;
    Context c;

    ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());

    public CustomAdaptorApps(Context context, ArrayList<AppsModel> app, String text_cond, UninstallInterface uninstallInterface) {
        this.text_type = text_cond;
        this.apps_list = app;
        this.mDisplayedValues = app;
        this.uninstallInterface = uninstallInterface;
        this.c = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_layout, parent, false);
        viewHolder vH = new viewHolder(v);
        return vH;
    }


    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.appList_item.setText(apps_list.get(position).getAppname());
        holder.icon.setImageDrawable(apps_list.get(position).getIcon_of_app());

        if (text_type == "Uninstall") {
            holder.update.setText("Uninstall");
        } else if (text_type == "update_after_scan") {
            holder.update.setText("Go to Playstore");
        }

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                holder.progress_installed.setVisibility(VISIBLE);
                if (text_type == "Update") {
                    boolean connected = false;
                    ConnectivityManager connectivityManager = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
                    }
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                    } else
                        connected = false;

                    if (connected) {
                        checkPlaystoreVersion(apps_list.get(position), position);

                    } else {
                        holder.progress_installed.setVisibility(VISIBLE);
                        Toast.makeText(c.getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } else if (text_type == "Uninstall") {
                    uninstallInterface.deleteApp(apps_list, position);
                } else if (text_type == "update_after_scan") {
                    holder.context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apps_list.get(position).getPackageName())));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return apps_list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Filter searchFilter() {
        Filter filter = new Filter() {


            @NonNull
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                ArrayList<AppsModel> FilteredArrList = new ArrayList<>();

                if (constraint == "" || constraint.toString().isEmpty()) {
                    FilteredArrList.addAll(mDisplayedValues);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim().replace(" ", "");
                    for (int i = 0; i < mDisplayedValues.size(); i++) {
                        if (mDisplayedValues.get(i).getAppname().toLowerCase().contains(filterPattern)) {
                            FilteredArrList.add(mDisplayedValues.get(i));
                        }
                    }
//                    System.out.println("size of new list is : " + results.count);
                }
                results.values = FilteredArrList;
                return results;
            }


            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                apps_list = new ArrayList<AppsModel>();
                apps_list.addAll((ArrayList<AppsModel>) results.values);
                notifyDataSetChanged();  // notifies the data with new filtered values
            }
        };
        return filter;
    }

    @Override
    public Filter getFilter() {
        return searchFilter();
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        TextView appList_item;
        ImageView icon;
        TextView update;
        Context context;
        Toolbar t;
        ProgressBar progress_installed;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            appList_item = itemView.findViewById(R.id.appListItem);
            icon = itemView.findViewById(R.id.icon_apps);
            update = itemView.findViewById(R.id.update_app_id);
            t = itemView.findViewById(R.id.t2);
            progress_installed = (ProgressBar) itemView.findViewById(R.id.progress_ins);
        }
    }

    private String getLastUpdateDate(int i) {
        PackageManager pm = c.getApplicationContext().getPackageManager();
        PackageInfo pk = null;

        try {
            pk = pm.getPackageInfo(apps_list.get(i).getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        long updateTimeInMilliseconds = 0; // install time is conveniently provided in milliseconds

        updateTimeInMilliseconds = pk.lastUpdateTime;

        Date date = new Date(updateTimeInMilliseconds);
        SimpleDateFormat df2 = new SimpleDateFormat("MMM d, yyyy");
        String dateText = df2.format(date);
        System.out.println("***********Installed app version Date : " + dateText);
        return dateText;
    }

    public void checkPlaystoreVersion(AppsModel model, int i) {
        ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        mExecutorService.execute(() -> {
            String newdate = null;
            try {
                System.out.println("*******Checking playstore version for : " + model.getPackageName());
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + model.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();

                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Updated on");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newdate = sibElemet.text();
                                System.out.println("+++++++++new Version : " + newdate);

                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("************" + "There is no playstore version available for this app");
                    Toast.makeText(c, "There is no playstore version available for this app", Toast.LENGTH_SHORT).show();
                }
                System.out.println("**Document : " + document.title());
            } catch (Exception e) {
                newdate = "Oct 23, 2000";
//                Log.e("error", e.toString());
            }
            String finalNewdate = newdate;
            handler.post(() -> compareVersions(i, finalNewdate));
        });
    }


    private void compareVersions(int i, String playstoreDate) {
        String installedDate = getLastUpdateDate(i);
        try {
            mExecutorService.execute(() -> {

                Log.d("installed", installedDate);
                Log.d("playstore", playstoreDate);
//                if (playstoreDate == null) {
//                    System.out.println("There is no playstore version available for this app");
//                    Toast.makeText(c, "There is no playstore version available for this app", Toast.LENGTH_SHORT).show();
//                }
                Date installed_app_date = null;
                Date playstore_app_date = null;
                try {
                    installed_app_date = new SimpleDateFormat("MMM d, yyyy")
                            .parse(installedDate);

                    playstore_app_date = new SimpleDateFormat("MMM d, yyyy")
                            .parse(playstoreDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long installed_miliseconds = installed_app_date.getTime();
                long playstore_miliseconds = playstore_app_date.getTime();

                System.out.println(installed_app_date);
                System.out.println(playstore_app_date);

                Intent intent = new Intent(c, ActivityLatestUpdate.class);
                System.out.println("*********PACKAGE NAME of installed app: " + apps_list.get(i).getPackageName());
                intent.putExtra("model", apps_list.get(i));
                handler.post(() -> {

                    if (installed_miliseconds < playstore_miliseconds) {
                        intent.putExtra("button_update", "Update Now");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                        c.startActivity(intent);
                    } else if (installed_miliseconds > playstore_miliseconds) {
                        intent.putExtra("button_update", "App up to date");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        c.startActivity(intent);
                    } else if (installed_miliseconds == playstore_miliseconds) {
                        intent.putExtra("button_update", "App up to date");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        c.startActivity(intent);
                    } else {
                        Toast.makeText(c, "No need to update App", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } catch (NullPointerException n) {
            Log.e("playDate", "Playstore Date is null");
        }
    }
}
