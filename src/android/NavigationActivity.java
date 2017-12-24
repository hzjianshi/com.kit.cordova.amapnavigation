package com.kit.cordova.navigationService;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.util.Log;


import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;

import com.kit.cordova.amapnavigation.AMapNavigation;

public class NavigationActivity extends Activity implements
        AMapNaviListener, AMapNaviViewListener {
    //导航View
    private AMapNaviView mAmapAMapNaviView;
    //是否为模拟导航
    private boolean mIsEmulatorNavi = false;
    // 导航类型 0 驾车导航，1 步行导航，2 骑行导航
    private int mNavWay = 0;
    //记录由哪个页面跳转而来，处理返回键
    private int mCode = -1;

    //起点终点
    private NaviLatLng mNaviStart;
    private NaviLatLng mNaviEnd;
    //起点终点列表
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("result", "onCreate");
        AMapNavi.getInstance(this).setAMapNaviListener(this);
        Intent intent = getIntent();
        mNaviStart = new NaviLatLng(Float.parseFloat(intent.getStringExtra("NaviStartLat")), Float.parseFloat(intent.getStringExtra("NaviStartLng")));
        mNaviEnd = new NaviLatLng(Float.parseFloat(intent.getStringExtra("NaviEndLat")), Float.parseFloat(intent.getStringExtra("NaviEndLng")));
        LinearLayout l = new LinearLayout(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        l.setLayoutParams(layoutParams);

        //判断实时还是模拟
        if (intent.getStringExtra("NavType").equals("0")) {
            mIsEmulatorNavi = true;
        } else {
            mIsEmulatorNavi = false;
        }

        // 导航类型
        mNavWay = Integer.parseInt(intent.getStringExtra("NavWay"));

        mAmapAMapNaviView = new AMapNaviView(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        l.addView(mAmapAMapNaviView, lp);

        setContentView(l);
        init(savedInstanceState, mNaviStart, mNaviEnd);
    }

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState, NaviLatLng mNaviStart, NaviLatLng mNaviEnd) {
        mAmapAMapNaviView.onCreate(savedInstanceState);
        mAmapAMapNaviView.setAMapNaviViewListener(this);
        switch (mNavWay) {
            case 0:
                Log.i("result", "dirver");

                mStartPoints.add(mNaviStart);
                mEndPoints.add(mNaviEnd);
                AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints, mEndPoints, null, PathPlanningStrategy.DRIVING_AVOID_CONGESTION);

                /**
                 * 驾车路线规划策略
                 * PathPlanningStrategy.DRIVING_AVOID_CONGESTION            单路径-时间优先 。规避拥堵的路线（考虑实时路况）
                 * ~.DRIVING_SINGLE_ROUTE_AVOID_HIGHSPEED                   单路径,【不走高速】
                 * ~.DRIVING_SINGLE_ROUTE_AVOID_HIGHSPEED_COST              单路径,【不走高速&躲避收费】
                 * ~.DRIVING_SINGLE_ROUTE_AVOID_CONGESTION_COST             单路径,【躲避收费&躲避拥堵】
                 * ~.DRIVING_SINGLE_ROUTE_AVOID_HIGHSPEED_COST_CONGESTION   单路径,【不走高速&躲避收费&躲避拥堵】
                 */


                return;
            case 1:
                Log.i("result", "walk");
                AMapNavi.getInstance(this).calculateWalkRoute(mNaviStart, mNaviEnd);
                return;
            case 2:
                Log.i("result", "ride");
                AMapNavi.getInstance(this).calculateRideRoute(mNaviStart, mNaviEnd);
                return;
            default:
                Log.i("result", "unknow Way");
                return;
        }
    }


    @Override
    public void onCalculateRouteFailure(int arg0) {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onReCalculateRouteForYaw() {
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteSuccess() {
        if (mIsEmulatorNavi) {
            Log.i("result", "模拟");
            // 设置模拟速度
            AMapNavi.getInstance(this).setEmulatorNaviSpeed(60);
            // 开启模拟导航
            AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);

        } else {
            Log.i("result", "实时");
            // 开启实时导航
            AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
        }
    }

    @Override
    public void onArriveDestination() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onArrivedWayPoint(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEndEmulatorNavi() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInitNaviFailure() {
        // TODO Auto-generated method stub
        Log.i("result", "导航失败");
    }

    @Override
    public void onInitNaviSuccess() {
        // TODO Auto-generated method stub
        Log.i("result", "导航注册成功");
    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {
        // TODO Auto-generated method stub
        AMapNavigation.getInstance().keepCallback(arg0.getCoord());
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {
        // TODO Auto-generated method stub

    }

    public void onStartNavi(int arg0) {
        // TODO Auto-generated method stub
        //mSpeechSynthesizer.startSpeaking("运维托管导航启动", mTtsListener);
        Log.i("result", "启动导航");
    }

    @Override
    public void onTrafficStatusUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] infoArray) {

    }

    @Override
    public void onPlayRing(int type) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] infoArray) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
    }

    @Override
    public void hideCross() {
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {

    }

    //-----------------------------导航界面回调事件------------------------

    /**
     * 导航界面返回按钮监听
     */
    @Override
    public void onNaviCancel() {
        this.setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviMapMode(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNaviTurnClick() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNextRoadClick() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onScanViewButtonClick() {
        // TODO Auto-generated method stub
    }

    // ------------------------------生命周期方法---------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAmapAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAmapAMapNaviView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAmapAMapNaviView.onPause();
        AMapNavi.getInstance(this).stopNavi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAmapAMapNaviView.onDestroy();
    }

    @Override
    public void onLockMap(boolean arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {
        // TODO Auto-generated method stub
    }
}
