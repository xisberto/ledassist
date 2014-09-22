package net.xisberto.ledassist.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.xisberto.ledassist.R;

public class AboutFragment extends Fragment {
    private View mView;

    public AboutFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String version;
        try {
            version = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "";
        }

        ((TextView)mView.findViewById(R.id.textVersion))
                .setText(getString(R.string.version, version));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_about, container, false);

        TextView textDeviceInfo = (TextView) mView.findViewById(R.id.textDeviceInfo);
        final String info = getString(R.string.device_info, Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE);
        textDeviceInfo.setText(info);

        mView.findViewById(R.id.buttonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse("mailto:"))
                        .putExtra(Intent.EXTRA_EMAIL, new String[] {"xisberto+ledassist@gmail.com"})
                        .putExtra(Intent.EXTRA_SUBJECT, "Led Assist doesn't work")
                        .putExtra(Intent.EXTRA_TEXT, info);
                startActivity(Intent.createChooser(email, getString(R.string.send)));
            }
        });

        return mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            getActivity().getActionBar().setHomeButtonEnabled(false);
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
}
