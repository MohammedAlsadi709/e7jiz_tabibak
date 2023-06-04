package com.alsadimoh.graduationproject.fragments

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.CalenderAdapter
import com.alsadimoh.graduationproject.adapters.TimeHoursAdapter
import com.alsadimoh.graduationproject.databinding.FragmentBookingAppointmentBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userBooking.*
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*


class BookingAppointmentFragment : Fragment(R.layout.fragment_booking_appointment) {

    private val text = "BookingAppointmentFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentBookingAppointmentBinding
    private var selectedPosition = -1
    private var selectedPositionForTime = -1
    private lateinit var bookingDate: String
    private lateinit var bookingDay: String
    private var newBookingTime: String? = null
    val args: BookingAppointmentFragmentArgs by navArgs()
    private var timeHoursAdapter: TimeHoursAdapter? = null

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookingAppointmentBinding.inflate(inflater, container, false)

        initViewModel()
        getBookingsForDayResponse()
        getAddNewBookingResponse()
        getEditBookingForUserResponse()

        binding.txtDoctorName.text = args.doctor.name
        binding.txtDoctorTitle.text = args.doctor.spec
        binding.txtPrice.text = "${args.doctor.price} شيكل"

        CommonConstants.loadImageWithPicasso(args.doctor.image, binding.imgDoctor)

        binding.txtMonthAndYear.text = SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())

        var year = SimpleDateFormat("yyyy", Locale.US).format(Date()).toInt()
        val actualYear = year
        val day = SimpleDateFormat("d", Locale.US).format(Date()).toInt()
        var selectedMonth = SimpleDateFormat("M", Locale.US).format(Date()).toInt()
        val actualMonth = selectedMonth
        hideShowBackBtn(actualMonth, selectedMonth, actualYear, year)

        val data = getDays(day, actualMonth, selectedMonth, year, actualYear)
        data[0].isSelected = true
        selectedPosition = 0
        val selectMonthFormatter = SimpleDateFormat("yyyy-M-d", Locale.US)
        val bookingDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val bookingDayFormatter = SimpleDateFormat("EEEE", Locale("ar"))
        bookingDate = bookingDateFormatter.format(selectMonthFormatter.parse(data[0].date)!!)
        bookingDay = bookingDayFormatter.format(selectMonthFormatter.parse(data[0].date)!!)

        viewModel.getBookingsForDay(
            GetBookingsForDay(
                args.doctor.doctor_id,
                bookingDay,
                bookingDate
            )
        )

        val calenderAdapter = CalenderAdapter(requireActivity(), data)
        binding.rvCalenderDays.adapter = calenderAdapter
        binding.rvCalenderDays.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)


        calenderAdapter.onClickItem = { model: CalenderDaysAdapterModel ->
            if (selectedPosition != -1) {
                calenderAdapter.data[selectedPosition].isSelected = false
                calenderAdapter.notifyItemChanged(selectedPosition)
            }
            newBookingTime = null
            calenderAdapter.data[model.id - 1].isSelected = true
            selectedPosition = model.id - 1
            calenderAdapter.notifyItemChanged(selectedPosition)
            bookingDate = bookingDateFormatter.format(selectMonthFormatter.parse(model.date)!!)
            bookingDay = bookingDayFormatter.format(selectMonthFormatter.parse(model.date)!!)
            viewModel.getBookingsForDay(
                GetBookingsForDay(
                    args.doctor.doctor_id,
                    bookingDay,
                    bookingDate
                )
            )
        }


        binding.btnNext.setOnClickListener {

            if (selectedMonth < 12) {
                selectedMonth += 1
            } else {
                selectedMonth = 1
                year += 1
            }
            calenderAdapter.data = getDays(day, actualMonth, selectedMonth, year, actualYear)
            calenderAdapter.notifyDataSetChanged()
            binding.txtMonthAndYear.text = SimpleDateFormat("MMMM", Locale.US).format(
                SimpleDateFormat("M", Locale.US).parse(selectedMonth.toString())!!
            ) + " $year"

            hideShowBackBtn(actualMonth, selectedMonth, actualYear, year)
        }

        binding.btnBack.setOnClickListener {

            if (selectedMonth > 1) {
                selectedMonth -= 1
            } else {
                selectedMonth = 12
                year -= 1
            }
            calenderAdapter.data = getDays(day, actualMonth, selectedMonth, year, actualYear)
            calenderAdapter.notifyDataSetChanged()
            binding.txtMonthAndYear.text = SimpleDateFormat("MMMM", Locale.US).format(
                SimpleDateFormat("M", Locale.US).parse(selectedMonth.toString())!!
            ) + " $year"

            hideShowBackBtn(actualMonth, selectedMonth, actualYear, year)
        }

        if (!(args.isFromEdit)) {
            binding.btnTakeTime.text = "احجز الآن"
        } else {
            binding.btnTakeTime.text = "حفظ التعديل"
        }

        binding.btnTakeTime.setOnClickListener {
            if (newBookingTime != null) {
                if (!(args.isFromEdit)) {

                    viewModel.addNewBookings(NewBookingRequestModel(args.doctor.doctor_id, bookingDate, newBookingTime!!))

                } else {
                    viewModel.getEditBookingForUser(
                        EditBookingRequestModel(
                            args.bookingId, bookingDate,
                            newBookingTime!!
                        )
                    )
                }
            } else {
                CommonConstants.showCustomToast(requireActivity(), "يرجى تحديد الوقت قبل الحجز","warning")

            }
        }



        return binding.root
    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getBookingsForDayResponse() {
        viewModel.getBookingsForDayResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data!!.items
                    if (response.isNotEmpty()) {
                        binding.rvTimes.visibility = View.VISIBLE
                        setBookingItems(response as MutableList<AvailableTimesForAddNewBooking>)
                        binding.layoutEmpty.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvTimes.visibility = View.GONE
                    }
                    newBookingTime = null
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    if (timeHoursAdapter != null) {
                        timeHoursAdapter!!.data.clear()
                        timeHoursAdapter!!.notifyDataSetChanged()
                    }
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }


    private fun getAddNewBookingResponse() {
        viewModel.getAddNewBookingsResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val action =
                        BookingAppointmentFragmentDirections.actionBookingAppointmentFragmentToBookingSuccessfullyFragment()
                    findNavController().navigate(action)
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


    private fun getEditBookingForUserResponse() {
        viewModel.getEditBookingForUserResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val message = it.data!!.message
                    CommonConstants.showCustomToast(requireActivity(),  message,"success")
                    findNavController().popBackStack()
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                    findNavController().popBackStack()
                }
            }
        }
    }


    private fun getDays(
        day: Int,
        actualMonth: Int,
        month: Int,
        year: Int,
        actualYear: Int
    ): MutableList<CalenderDaysAdapterModel> {

        val daysNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            YearMonth.of(year, month).lengthOfMonth()
        } else {
            GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        val array = mutableListOf<CalenderDaysAdapterModel>()
        var counterForId = 1 // because some months start with actual day
        if (actualMonth == month && year == actualYear) {
            for (i in day..daysNumber) {
                array.add(CalenderDaysAdapterModel(counterForId, "$year-$month-$i", i))
                counterForId++
            }
        } else {
            for (i in 1..daysNumber) {
                array.add(CalenderDaysAdapterModel(counterForId, "$year-$month-$i", i))
                counterForId++
            }
        }
        return array
    }

    private fun hideShowBackBtn(
        actualMonth: Int,
        selectedMonth: Int,
        year: Int,
        selectedYear: Int
    ) {
        if (selectedMonth == actualMonth && year == selectedYear) {
            binding.btnBack.visibility = View.INVISIBLE
        } else {
            binding.btnBack.visibility = View.VISIBLE
        }
    }

    private fun setBookingItems(data: MutableList<AvailableTimesForAddNewBooking>) {
        timeHoursAdapter = TimeHoursAdapter(requireActivity(), data)
        binding.rvTimes.adapter = timeHoursAdapter
        binding.rvTimes.layoutManager = GridLayoutManager(requireActivity(), 4)

        timeHoursAdapter!!.onClickItem = { position, model ->
            if (selectedPositionForTime != -1) {
                timeHoursAdapter!!.data[selectedPositionForTime].isSelected = false
                timeHoursAdapter!!.notifyItemChanged(selectedPositionForTime)
            }
            timeHoursAdapter!!.data[position].isSelected = true
            selectedPositionForTime = position
            timeHoursAdapter!!.notifyItemChanged(selectedPositionForTime)
            newBookingTime = model.time
        }
    }

}