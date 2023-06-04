package com.alsadimoh.graduationproject.fragments.doctorFragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.doctorsAdapters.DoctorHomeBookingAdapter
import com.alsadimoh.graduationproject.adapters.doctorsAdapters.HeaderDaysForDoctorHomeAdapter
import com.alsadimoh.graduationproject.databinding.FragmentIncommingDoctorBookingBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.BookingModelForDoctor
import com.alsadimoh.graduationproject.retrofit.models.userBooking.CalenderDaysAdapterModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

class IncomingDoctorBookingFragment : Fragment(R.layout.fragment_incomming_doctor_booking) {

    lateinit var binding: FragmentIncommingDoctorBookingBinding
    private lateinit var viewModel: MyViewModel
    val text = "IncomingDoctorBookingFragment"
    private var canSendRequest = true
    private val dateForRequest = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onResume() {
        super.onResume()
        if (canSendRequest){
            viewModel.getIncomingBookingForDoctor(dateForRequest.format(Date()))
            canSendRequest = false // عشان ما يرفرش كل ما يلف الفيو بيجر
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentIncommingDoctorBookingBinding.inflate(inflater, container, false)
        canSendRequest = true // عشان لما يرجع من شاشة اخرى يرفرش
        initViewModel()
        getIncomingBookingForDoctorResponse()

        val date = SimpleDateFormat("yyyy-M-d", Locale.US)
        //val fullDateForDay = SimpleDateFormat("yyyy MMMM d EEEE", Locale.US)
        val fullDateForDay = SimpleDateFormat("EEEE d MMMM yyyy", Locale("ar"))
        val day = SimpleDateFormat("d", Locale.US)
        val month = SimpleDateFormat("M", Locale.US)
        val year = SimpleDateFormat("yyyy", Locale.US)

        val calendarForTomorrow = Calendar.getInstance()
        calendarForTomorrow.add(Calendar.DAY_OF_YEAR, 1)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 31) //calendar.add(Calendar.MONTH, 1) الشهر القادم

       // val dayAfter31Days = day.format(calendar.time)
        val monthAfter31Days = month.format(calendar.time)
        val yearAfter31Days = year.format(calendar.time)

        val days = getDays(day.format(Date()).toInt(),month.format(Date()).toInt(),monthAfter31Days.toInt(),year.format(Date()).toInt(),yearAfter31Days.toInt())
        var selectedPosition = 0 // أول يوم هو الي مختار ك ديفولت
        days[0].isSelected = true

        val currentDate =date.format(Date())
        val tomorrowDate = date.format(calendarForTomorrow.time)

        val adapter = HeaderDaysForDoctorHomeAdapter(requireActivity(),days,currentDate, tomorrowDate)
        binding.rvHeaderDays.adapter = adapter
        binding.rvHeaderDays.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)

        adapter.onClickItem = { model ->

            if (selectedPosition != model.id - 1){
                viewModel.getIncomingBookingForDoctor(dateForRequest.format(date.parse(model.date)!!))
            }

            if (selectedPosition != -1) {
                adapter.data[selectedPosition].isSelected = false
                adapter.notifyItemChanged(selectedPosition)
            }

            adapter.data[model.id - 1].isSelected = true
            selectedPosition = model.id - 1
            adapter.notifyItemChanged(selectedPosition)

            when (model.date) {
                currentDate -> {
                    binding.txtBookingDay.text = "حجوزات اليوم"
                }
                tomorrowDate -> {
                    binding.txtBookingDay.text = "حجوزات غداً"
                }
                else -> {
                    binding.txtBookingDay.text = "حجوزات ${model.date}"
                }
            }
            binding.txtFullDateForBookingDay.text = fullDateForDay.format(date.parse(model.date)!!)
        }

        return binding.root
    }


    private fun getDays(day:Int,month:Int,monthAfter31Days: Int,year:Int,yearAfter31Days:Int): MutableList<CalenderDaysAdapterModel> {

        val daysNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            YearMonth.of(year, month).lengthOfMonth()
        } else {
            GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        val array = mutableListOf<CalenderDaysAdapterModel>()
        var counterForId = 1 // because some months start with actual day

            for (i in day..daysNumber) {
                array.add(CalenderDaysAdapterModel(counterForId, "$year-$month-$i",i))
                counterForId++ //
            }

        val stillDays = 31 - (counterForId-1) //

        for (i in 1..stillDays){
            array.add(CalenderDaysAdapterModel(counterForId, "$yearAfter31Days-$monthAfter31Days-$i",i))
            counterForId++
        }

        return array
    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    private fun getIncomingBookingForDoctorResponse() {
        viewModel.getIncomingBookingForDoctorResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data!!.items
                    setAdapterItems(response as MutableList<BookingModelForDoctor>)
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }

    private fun setAdapterItems(data: MutableList<BookingModelForDoctor>){
        val bookingAdapter = DoctorHomeBookingAdapter(requireActivity(),data)
        binding.rvIncomingBookings.adapter = bookingAdapter
        binding.rvIncomingBookings.layoutManager  = LinearLayoutManager(requireActivity())

        if (bookingAdapter.data.isNotEmpty()){
            binding.layoutEmpty.visibility = View.GONE
        }else{
            binding.layoutEmpty.visibility = View.VISIBLE
        }

        bookingAdapter.onClickBookingItem= { model ->
            val action = IncomingDoctorBookingFragmentDirections.actionGlobalBookingDetailsFragment(model.id,"incoming")
            findNavController().navigate(action)
        }
    }

}