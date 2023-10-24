package com.example.mungnyang.Fragment.ShareToDiary

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.mungnyang.Fragment.FriendInfo
import com.example.mungnyang.Fragment.ShareToWalking.shareToWalkingFragment

class CommunityPagerAdapter (fm: FragmentManager, private val accountEmail: String, private val userNickName: String,
                             private val imageURL: String, private val friendInfoList: List<FriendInfo>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = shareToDiaryFragment()
                val args = Bundle()
                args.putString("accountEmail", accountEmail)
                args.putString("userNickName", userNickName)
                args.putString("imageURL", imageURL)
                args.putParcelableArrayList("friendInfoList", ArrayList(friendInfoList))
                fragment.arguments = args
                fragment
            }
            1 -> {
                val fragment = shareToWalkingFragment()
                val args = Bundle()
                args.putString("accountEmail", accountEmail)
                args.putString("userNickName", userNickName)
                args.putString("imageURL", imageURL)
                args.putParcelableArrayList("friendInfoList", ArrayList(friendInfoList))
                fragment.arguments = args
                fragment
            } // "멍!냥 산책" 페이지 프래그먼트
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getCount(): Int {
        return 2 // 두 개의 페이지
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "일지 공유"
            1 -> "멍!냥 산책"
            else -> ""
        }
    }
}
