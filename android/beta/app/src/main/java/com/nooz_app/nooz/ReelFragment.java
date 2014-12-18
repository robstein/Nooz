package com.nooz_app.nooz;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rob on 11/30/14.
 */
public class ReelFragment extends Fragment {

    public ReelFragment() {
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reel, container, false);
        return rootView;
    }

    //initViews();
    //initViewListeners();
    //initPager();
    //mIconProfile.setImageUrl(GlobalConstant.PROFILE_URL + mUserId, mImageLoader);

}
