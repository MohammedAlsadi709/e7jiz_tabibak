package com.alsadimoh.graduationproject

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.collection.arraySetOf
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.changeAppFontsSize
import com.alsadimoh.graduationproject.classes.CommonConstants.logOut
import com.alsadimoh.graduationproject.databinding.ActivityMainBinding
import com.alsadimoh.graduationproject.classes.CommonConstants.onEnterHomeUpdateDrawerHeader
import com.alsadimoh.graduationproject.classes.CommonConstants.onOpenFragmentsUpdateMenuItemsIcons
import com.alsadimoh.graduationproject.classes.CommonConstants.onOpenFragmentChangeEmptyMenuItemIconBitmapDrawable
import com.alsadimoh.graduationproject.classes.CommonConstants.onReceiverNotificationShowRatingDialog
import com.alsadimoh.graduationproject.classes.CommonConstants.onWifiStateChanged
import com.alsadimoh.graduationproject.classes.CommonConstants.updateMenuItemsForDoctor
import com.alsadimoh.graduationproject.classes.CommonConstants.updateMenuItemsForUser
import com.alsadimoh.graduationproject.classes.FirebaseNotificationsService
import com.alsadimoh.graduationproject.classes.MyReceiver
import com.alsadimoh.graduationproject.databinding.CustomRateDoctorDialogBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.RateDoctorRequestModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    companion object {
        var isNightMode: Boolean
            get() {
                return CommonConstants.myShared.getBoolean(CommonConstants.isNightMode, false)
            }
            set(value) {
                CommonConstants.putBooleanPref(CommonConstants.isNightMode, value)
            }

        var appFontSize: Float
            get() {
                return CommonConstants.myShared.getFloat(CommonConstants.appFontSize, 1.0F)
            }
            set(value) {
                CommonConstants.putFloatPref(CommonConstants.appFontSize, value)
            }

    }

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MyViewModel
    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var myReceiver: MyReceiver
    private var loggedOut = false


    //لاخفاء زر الرجوع
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CommonConstants.myShared =
            getSharedPreferences(CommonConstants.mySharedPrefKey, Context.MODE_PRIVATE)

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }

        if (appFontSize != 1.0F) {
            changeAppFontsSize(this, resources.configuration, appFontSize)
        }

        myReceiver = MyReceiver()
        val i = IntentFilter()
        i.addAction("com.alsadimoh.graduationproject.showratedialog")
        i.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(myReceiver, i)

        saveToken()

        mAuth = FirebaseAuth.getInstance()

        setDefaultLanguage(this)

        initViewModel()
        getLogoutResponse()
        getRateDoctorResponse()

        onWifiStateChanged = { isConnected->
          if (isConnected){
              binding.cardNoWIFI.visibility = View.GONE
          }else{
              binding.cardNoWIFI.visibility = View.VISIBLE
          }
        }

        onReceiverNotificationShowRatingDialog = { context,doctor_name,doctor_id ->

            if (CommonConstants.isAppRunning(context, "com.alsadimoh.graduationproject")) {

                val dialog = Dialog(context)
                val dialogBinding =
                    CustomRateDoctorDialogBinding.inflate(LayoutInflater.from(context))
                dialog.setContentView(dialogBinding.root)

                dialogBinding.txtRate.text = "تقييم الطبيب $doctor_name"

                var rate = 0F
                dialogBinding.dialogRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
                    rate = rating
                }

                dialogBinding.btnSend.setOnClickListener {
                    if (dialogBinding.txtComment.text.isNotEmpty()) {
                        CommonConstants.putBooleanPref(CommonConstants.isNotificationForDoneReceived,false)
                        val comment = dialogBinding.txtComment.text.toString()
                        dialog.dismiss()
                        viewModel.rateDoctor(RateDoctorRequestModel(doctor_id,rate,comment))
                    } else {
                        CommonConstants.showCustomToast(this, "يرجى ادخال التعليق","warning")
                    }
                }
                dialog.setOnDismissListener {
                    CommonConstants.putBooleanPref(CommonConstants.isNotificationForDoneReceived,false)
                }

                dialog.show()
                val window: Window = dialog.window!!
                window.setLayout(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.WRAP_CONTENT
                )

            }
        }



        onEnterHomeUpdateDrawerHeader = {
            // التحكم في الهيدر
            val image = CommonConstants.myShared.getString(CommonConstants.userImage, null)
            val name = CommonConstants.myShared.getString(CommonConstants.userName, "مستخدم")
            val phone =
                CommonConstants.myShared.getString(CommonConstants.userPhoneNumber, "059*******")
            val header = binding.navView.getHeaderView(0)
            val imageView = header.findViewById<ImageView>(R.id.userImage)

            if (image == null) {
                imageView.setImageResource(R.mipmap.ic_user_profile_default)
            } else {
                CommonConstants.loadImageWithPicasso(image, imageView)
            }
            header.findViewById<TextView>(R.id.userName).text = name
            header.findViewById<TextView>(R.id.userPhoneNumber).text = phone
        }

        val drawerMenu = binding.navView.menu
        updateMenuItemsForUser = {
            drawerMenu.findItem(R.id.homeFragment).isVisible = true
            drawerMenu.findItem(R.id.myTimesFragment).isVisible = true
            drawerMenu.findItem(R.id.userProfileFragment).isVisible = true
            drawerMenu.findItem(R.id.doctorProfileForDoctorUserFragment).isVisible = false
            drawerMenu.findItem(R.id.doctorHomeFragment).isVisible = false
            drawerMenu.findItem(R.id.reviewBookingRequestsForDoctor).isVisible = false
        }

        updateMenuItemsForDoctor = {
            drawerMenu.findItem(R.id.homeFragment).isVisible = false
            drawerMenu.findItem(R.id.myTimesFragment).isVisible = false
            drawerMenu.findItem(R.id.userProfileFragment).isVisible = false
            drawerMenu.findItem(R.id.doctorProfileForDoctorUserFragment).isVisible = true
            drawerMenu.findItem(R.id.doctorHomeFragment).isVisible = true
            drawerMenu.findItem(R.id.reviewBookingRequestsForDoctor).isVisible = true

        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(
            //بنحدد شو هي الفراقمنت الي ما بدنا فيها الرجوع
            arraySetOf(
                R.id.homeFragment,
                R.id.myTimesFragment,
                R.id.userProfileFragment,
                R.id.doctorHomeFragment,
                R.id.doctorProfileForDoctorUserFragment,
                R.id.chattedUsersFragment,
                R.id.reviewBookingRequestsForDoctor,
                R.id.notificationsFragment
            ),
            binding.drawerLayout
        )


        // بنمرر الاب بار كونفق عشان نشيل زر الرجوع عند الانتقال
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logoutItem -> {
                    val alert = AlertDialog.Builder(this)
                    alert.setMessage("هل أنت متأكد من عملية تسجيل الخروج")
                    alert.setTitle("تسجيل الخروج")
                    alert.setIcon(R.mipmap.export_gray)

                    alert.setPositiveButton("تسجيل الخروج") { dialogInterface, _ ->
                        loggedOut = true
                        mAuth.signOut()
                        viewModel.logout()
                        dialogInterface.cancel()
                    }

                    alert.setNegativeButton("ابقاء الجلسة") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    alert.create().show()


                }
                else -> {
                    NavigationUI.onNavDestinationSelected(it, navController)
                }
            }
            //Call the original listener
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        handleFragments()


    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        reInflateMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.saveAsFavorite -> {
                CommonConstants.onPutDoctorToBookmarksForUser?.invoke()
                true
            }
            else -> {
                return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(
                    item
                )//اون ناف ديستسنيشن عشان لو كان فراقمنت وحطينا الايدي تبع المنيو ايتم هو نفسه الايدي تاع الفراقمنت جوا الناف جراف ينتقل عليه لحاله
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun handleFragments() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in arrayOf(
                    R.id.splashFragment,
                    R.id.loggingTypeFragment,
                    R.id.bookingSuccessfullyFragment,
                    R.id.welcomePageFragmentForStartUp,
                    R.id.requestLocationPermissionFragment
                )
            ) {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
        }
    }

    private fun reInflateMenu(menu: Menu?) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                in arrayOf(R.id.homeFragment, R.id.doctorHomeFragment) -> {
                    menu!!.findItem(R.id.notificationsFragment).isVisible = true
                    menu.findItem(R.id.emptyItem).isVisible = false
                    menu.findItem(R.id.saveAsFavorite).isVisible = false
                    menu.findItem(R.id.settingsFragments).isVisible = false
                }

                in arrayOf(
                    R.id.doctorsOfCategoryFragment,
                    R.id.bookingDetailsFragment,
                    R.id.reviewsFragment
                ) -> {
                    onOpenFragmentsUpdateMenuItemsIcons = { icon ->
                        menu!!.findItem(R.id.emptyItem).setIcon(icon)
                    }

                    onOpenFragmentChangeEmptyMenuItemIconBitmapDrawable = { image ->
                        Picasso.get().load(image).into(object : com.squareup.picasso.Target {
                            override fun onBitmapLoaded(
                                bitmap: Bitmap?,
                                from: Picasso.LoadedFrom?
                            ) {
                                menu!!.findItem(R.id.emptyItem).icon =
                                    BitmapDrawable(resources, bitmap)
                            }

                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                        })
                    }

                    menu!!.findItem(R.id.emptyItem).isVisible = true
                    menu.findItem(R.id.notificationsFragment).isVisible = false
                    menu.findItem(R.id.saveAsFavorite).isVisible = false
                    menu.findItem(R.id.settingsFragments).isVisible = false
                }
                in arrayOf(R.id.doctorProfileFragment) -> {
                    onOpenFragmentsUpdateMenuItemsIcons = { icon ->
                        menu!!.findItem(R.id.saveAsFavorite).setIcon(icon)
                    }
                    menu!!.findItem(R.id.notificationsFragment).isVisible = false
                    menu.findItem(R.id.emptyItem).isVisible = false
                    menu.findItem(R.id.saveAsFavorite).isVisible = true
                    menu.findItem(R.id.settingsFragments).isVisible = false
                }
                in arrayOf(R.id.userProfileFragment, R.id.doctorProfileForDoctorUserFragment) -> {
                    menu!!.findItem(R.id.notificationsFragment).isVisible = false
                    menu.findItem(R.id.emptyItem).isVisible = false
                    menu.findItem(R.id.saveAsFavorite).isVisible = false
                    menu.findItem(R.id.settingsFragments).isVisible = true
                }
                else -> {
                    menu!!.findItem(R.id.notificationsFragment).isVisible = false
                    menu.findItem(R.id.emptyItem).isVisible = false
                    menu.findItem(R.id.saveAsFavorite).isVisible = false
                    menu.findItem(R.id.settingsFragments).isVisible = false
                }
            }
        }
    }

    private fun getLogoutResponse() {
        viewModel.getLogoutResponse().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (loggedOut){
                        loggedOut = false
                        CommonConstants.hideProgressDialog()
                        logOut()
                        val action = NavGraphDirections.actionGlobalLoggingTypeFragment2()
                        navController.navigate(action)
                    }
                }
                Status.LOADING -> {
                    CommonConstants.showProgressDialog(this)
                }
                Status.ERROR -> {
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(this,  it.message.toString(),"error")
                }
            }
        }
    }

    private fun getRateDoctorResponse() {
        viewModel.getRateDoctorResponse().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(this,  "شكراً على اعطائنا القليل من وقتك الثمين","info")
                }
                Status.LOADING -> {
                    CommonConstants.showProgressDialog(this)
                }
                Status.ERROR -> {
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(this,  it.message.toString(),"error")
                }
            }
        }
    }


    private fun setDefaultLanguage(context: Context) {
        val locale = Locale("ar")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    private fun saveToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            val token = it
            FirebaseNotificationsService.token = token
            Log.e("moh", "saveToken: $token")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

}