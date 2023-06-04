package com.alsadimoh.graduationproject.fragments.doctorFragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.myPic
import com.alsadimoh.graduationproject.classes.CommonConstants.onEnterHomeUpdateDrawerHeader
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.doctorsAdapters.TimesViewPagerForDoctorAdapter
import com.alsadimoh.graduationproject.databinding.FragmentDoctorHomeBinding
import com.alsadimoh.graduationproject.classes.CommonConstants.updateMenuItemsForDoctor
import com.alsadimoh.graduationproject.chatUtils.models.UserModelForRegister
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class DoctorHomeFragment : Fragment(R.layout.fragment_doctor_home) {

    lateinit var binding: FragmentDoctorHomeBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var imageProfile = myPic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentDoctorHomeBinding.inflate(inflater, container, false)


        val name = CommonConstants.myShared.getString(CommonConstants.userName,null)
        val password  = CommonConstants.myShared.getString(CommonConstants.userPassword,null)
        val email  = CommonConstants.myShared.getString(CommonConstants.userEmail,null)
        val userType  = CommonConstants.myShared.getString(CommonConstants.userType,"user")
        val image  = CommonConstants.myShared.getString(CommonConstants.userImage,null)

        try {
            if (image!= null){
                Picasso.get().load(image).into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        imageProfile = CommonConstants.bitmapToString(bitmap!!)
                    }
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                })
            }
        }catch (e:java.lang.Exception){}

        if (name != null && password != null && email!= null){

            val model = UserModelForRegister(null,null,name,email,imageProfile,"مرحباً أنا متوفر لمراسلتي!","متوفر",
                null,userType)

            mAuth = FirebaseAuth.getInstance()
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
                if (task.isSuccessful){
                    model.userId = mAuth.uid!!
                    addUserToFireStoreDatabase(model)
                }
            }.addOnFailureListener {
                mAuth.signInWithEmailAndPassword(email,password)
            }
        }



        onEnterHomeUpdateDrawerHeader?.invoke()
        updateMenuItemsForDoctor?.invoke()

        val adapter = TimesViewPagerForDoctorAdapter(requireActivity(),childFragmentManager)
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        return binding.root
    }
    private fun addUserToFireStoreDatabase(modelForRegister: UserModelForRegister){
        db = Firebase.firestore
        db.collection("registeredUsers").add(modelForRegister)
    }
}