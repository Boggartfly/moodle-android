package in.laer.moodle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadWebPageTask().execute();
                Snackbar.make(view, "Fetching Notices", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                TextView txtView = (TextView) findViewById(R.id.progressText);
                txtView.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        // ProgressDialog dialog = new ProgressDialog(getParent());
        // TextView txtView;
        // The variable is moved here, we only need it here while displaying the
        // progress dialog.

        ProgressBar dialog = (ProgressBar) findViewById(R.id.progressdialog);
        TextView txtView;

        @Override
        protected void onPreExecute() {
            dialog.setIndeterminate(true);
            dialog.setVisibility(View.VISIBLE);
            txtView = (TextView) findViewById(R.id.progressText);
            txtView.setMovementMethod(new ScrollingMovementMethod());

        }


        @Override
        protected String doInBackground(String... urls) {
            StringBuffer sb = new StringBuffer();
            try {
                List<Element> notices = new ArrayList<>();
                // List<Element> links = new ArrayList<>();
                // Get The Site and Parse it
                Document document = Jsoup.connect("http://moodle.siesgst.ac.in").get();
                notices.addAll(document.select(".forumpost").addClass("posting"));
                //notices.addAll(document.select("a"));
                // Select Table

                for (Element notice : notices) {
                    Iterator<Element> lines = notice.select("td").addClass("posting").iterator();
                    while (lines.hasNext()) {
                        Element line = lines.next();
                        //if(line.text().endsWith("\n"))
                        //if(!line.text().startsWith("\n"))


                        if (!line.text().isEmpty()) {
                            sb.append("*" + line.text() + "\n");
                        } else {
                            lines.hasNext();
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            String result = sb.toString();
            System.out.println(result);
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {


            Log.d("Notices", result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    txtView.setText(result);
                    dialog.setVisibility(View.GONE);
                }
            });
        }
    }

}
