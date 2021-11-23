package com.example.chain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

class MainActivity : AppCompatActivity() {

    private val pagerAdapterHome: ViewPager2AdapterHome by lazy { ViewPager2AdapterHome(this) }
    private val pagerAdapterLog: ViewPager2AdapterLog by lazy { ViewPager2AdapterLog(this) }
    private val pagerAdapterNotification: ViewPager2AdapterNotification by lazy { ViewPager2AdapterNotification(this) }



    companion object{
        //bottomNav管理用の番号　0->ホーム　1->記録　2->通知
        var navFlag: Int = 0

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_chain)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        viewpager2.offscreenPageLimit = 3
        viewpager2.adapter = pagerAdapterHome

        bottomNavConfig()

        tabConfig()//上のadapter接続の2行よりも後に置かないと動かない

        pagerAdapterHome.add(ViewPager2AdapterHome.FragmentName.WHITE)
        pagerAdapterHome.add(ViewPager2AdapterHome.FragmentName.RED)
        pagerAdapterHome.add(ViewPager2AdapterHome.FragmentName.GREEN)
        pagerAdapterLog.add(ViewPager2AdapterLog.FragmentName.WHITE)
        pagerAdapterLog.add(ViewPager2AdapterLog.FragmentName.RED)
        pagerAdapterLog.add(ViewPager2AdapterLog.FragmentName.GREEN)
        pagerAdapterNotification.add(ViewPager2AdapterNotification.FragmentName.WHITE)
        pagerAdapterNotification.add(ViewPager2AdapterNotification.FragmentName.RED)
        pagerAdapterNotification.add(ViewPager2AdapterNotification.FragmentName.GREEN)


    }

    //tabの文字を変化させる。
    private fun tabConfig(){
        TabLayoutMediator(tabLayout, viewpager2) { tab, position ->
            tab.text = when (pagerAdapterHome.fragmentList[position]) {
                ViewPager2AdapterHome.FragmentName.WHITE -> {
                    when(navFlag){
                        0->"勉強開始"
                        1->"時間"
                        2->"塾から"
                        else->"勉強開始"
                    }
                }

                ViewPager2AdapterHome.FragmentName.RED -> {
                    when(navFlag){
                        0->"目標"
                        1->"動画"
                        2->"運営から"
                        else->"時間"
                    }
                }


                ViewPager2AdapterHome.FragmentName.GREEN -> {
                    when(navFlag){
                        0->"予約"
                        1->"学習状況"
                        2->"？？？"
                        else->"予約"
                    }
                }


            }
        }.attach()

    }

    //BottomNavのボタンを押したときの設定
    fun bottomNavConfig(){
        bottomNavigationView.setOnItemSelectedListener {

            tabLayout.getTabAt(0)?.select()

            when (it.itemId) {
                R.id.home -> {
                    navFlag = 0
                    viewpager2.offscreenPageLimit = 3
                    viewpager2.adapter = pagerAdapterHome
                    //pagerAdapterHome.add(ViewPager2AdapterHome.FragmentName.WHITE)
                    //pagerAdapterHome.add(ViewPager2AdapterHome.FragmentName.RED)
                    //pagerAdapterHome.add(ViewPager2AdapterHome.FragmentName.GREEN)
                    Log.d("TAG", "いまここ０")
                    tabConfig()
                }
                R.id.log -> {
                    navFlag = 1
                    viewpager2.offscreenPageLimit = 3
                    viewpager2.adapter = pagerAdapterLog
                    //pagerAdapterLog.add(ViewPager2AdapterLog.FragmentName.WHITE)
                    //pagerAdapterLog.add(ViewPager2AdapterLog.FragmentName.RED)
                    //pagerAdapterLog.add(ViewPager2AdapterLog.FragmentName.GREEN)
                    Log.d("TAG", "いまここ１")
                    tabConfig()
                }
                R.id.notification -> {
                    navFlag = 2
                    viewpager2.offscreenPageLimit = 3
                    viewpager2.adapter = pagerAdapterNotification
                    //pagerAdapterNotification.add(ViewPager2AdapterNotification.FragmentName.WHITE)
                    //pagerAdapterNotification.add(ViewPager2AdapterNotification.FragmentName.RED)
                    //pagerAdapterNotification.add(ViewPager2AdapterNotification.FragmentName.GREEN)
                    Log.d("TAG", "いまここ２")
                    tabConfig()
                }

            }
            true
        }

    }

}