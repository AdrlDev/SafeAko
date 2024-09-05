package com.sprtcoding.safeako.welcome_page.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter
import com.sprtcoding.safeako.R

class ViewPagerAdapter(private val context: Context) : PagerAdapter() {
    private val images = intArrayOf(
        R.drawable.illustration,
        R.drawable.illustration2,
        R.drawable.illustration3
    )

    private val heading = intArrayOf(
        R.string.h1,
        R.string.h2,
        R.string.h3
    )

    private val description = intArrayOf(
        R.string.desc1,
        R.string.desc2,
        R.string.desc3
    )

    override fun getCount(): Int {
        return heading.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    @SuppressLint("KotlinNullnessAnnotation")
    @NonNull
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater.inflate(R.layout.slider_layout, container, false)

        val slideImg = v.findViewById<ImageView>(R.id.image)
        val titleText = v.findViewById<TextView>(R.id.tv_header)
        val textDesc = v.findViewById<TextView>(R.id.tv_desc)

        slideImg.setImageResource(images[position])
        titleText.setText(heading[position])
        textDesc.setText(description[position])

        container.addView(v)

        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}