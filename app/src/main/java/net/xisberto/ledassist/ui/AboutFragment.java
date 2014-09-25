package net.xisberto.ledassist.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

        ListView list = (ListView) mView.findViewById(R.id.list);
        list.setAdapter(new LicenseAdapter(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_about, container, false);

        return mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        prepareLicensesInformation();
    }

    private void prepareLicensesInformation() {
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

    public static class LicenseAdapter extends BaseAdapter {
        private Context mContext;
        private String[] mLibraryNames, mLibraryVersions;

        public LicenseAdapter(Context context) {
            mContext = context;
            mLibraryNames = mContext.getResources().getStringArray(R.array.library_names);
            mLibraryVersions = mContext.getResources().getStringArray(R.array.library_versions);
        }

        @Override
        public int getCount() {
            return mLibraryNames.length;
        }

        @Override
        public Object getItem(int position) {
            return mLibraryNames[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            ((TextView)convertView.findViewById(android.R.id.text1)).setText(mLibraryNames[position]);
            ((TextView)convertView.findViewById(android.R.id.text2)).setText(mLibraryVersions[position]);
            return convertView;
        }
    }
}
