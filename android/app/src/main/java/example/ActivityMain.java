package example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityMain extends Activity {
    boolean loginFragment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame, new FragmentLogin())
                    .commit();
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.gas).setVisible(!loginFragment);
        menu.findItem(R.id.lease).setVisible(!loginFragment);
        menu.findItem(R.id.logout).setVisible(!loginFragment);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                loginFragment = true;
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, new FragmentLogin())
                        .commit();
                invalidateOptionsMenu();
                break;
            case R.id.gas:
                DialogGas.newInstance().show(getFragmentManager(), null);
                break;
            case R.id.lease:
                DialogLease.newInstance().show(getFragmentManager(), null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
