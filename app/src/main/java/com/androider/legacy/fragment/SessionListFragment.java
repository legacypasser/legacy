package com.androider.legacy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androider.legacy.R;
import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.adapter.RecyclerListAdapter;
import com.androider.legacy.adapter.SessionListAdapter;
import com.androider.legacy.adapter.SimpleAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.Session;
import com.androider.legacy.data.User;
import com.androider.legacy.service.NetService;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SessionListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SessionListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SessionListFragment extends BaseListFragment implements SessionListAdapter.OnItemClickListner{

    public SessionListAdapter adapter = new SessionListAdapter();
    public static SessionListFragment instance;
    public static SessionListFragment newInstance(String param1, String param2) {
        SessionListFragment fragment = new SessionListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SessionListFragment(){
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        commonSet(rootView);
        swipeHolder.setEnabled(false);
        Session.drag();
        listSessions();
        return rootView;
    }

    public void listSessions(){
        if(Holder.talks.size() == 0){
            selfList.setAdapter(new SimpleAdapter(getResources().getString(R.string.empty_session)));
            return;
        }
        adapter.setOnclickListener(this);
        selfList.setAdapter(adapter);
        Iterator it  = Holder.talks.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry entry = (HashMap.Entry)it.next();
            adapter.addData((Session)entry.getValue());
        }
    }

    public void refreshSessions(Session item){
        if(item.affected){
            adapter.updateData(item);
            item.affected = false;
        }
    }

    public void refreshSessions(){
        if(!(selfList.getAdapter() instanceof SessionListAdapter)){
            adapter.setOnclickListener(this);
            selfList.setAdapter(adapter);
        }
        Iterator it  = Holder.talks.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry entry = (HashMap.Entry)it.next();
            refreshSessions((Session)entry.getValue());
        }
    }

    public void startPull(){
        Intent intent = new Intent(getActivity(), NetService.class);
        intent.putExtra(Constants.intentType, Constants.pullMsg);
        getActivity().startService(intent);
    }

    @Override
    public void onItemClick(int id) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("talker", id);
        getActivity().startActivity(intent);
    }
}
