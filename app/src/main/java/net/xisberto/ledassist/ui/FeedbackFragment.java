package net.xisberto.ledassist.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.xisberto.ledassist.R;

/**
 * Created by xisberto on 21/09/14.
 */
public class FeedbackFragment extends Fragment {
    private View mView;

    public FeedbackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_feedback, container, false);

        TextView textDeviceInfo = (TextView) mView.findViewById(R.id.textDeviceInfo);
        String info = getString(R.string.device_info, Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE);
//        info += Build.MANUFACTURER + "\n";
//        info += Build.MODEL + "\n";
//        info += Build.VERSION.RELEASE + "\n";
        textDeviceInfo.setText(info);

        return mView;
    }
}
