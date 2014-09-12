package com.example.NiePaoPao;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.example.NiePaoPao.UnderlinePageIndicator;



public class SampleUnderlinesDefault extends BaseSampleActivity {
    static NiePaoPaoGLSurfaceView mGLSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_underlines);
        mGLSurfaceView = new NiePaoPaoGLSurfaceView(this);
        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (UnderlinePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
}