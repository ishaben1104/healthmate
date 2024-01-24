package com.example.healthmate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentAppointmentsConfirm : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewComplete: RecyclerView
    private lateinit var recyclerViewCancel: RecyclerView
    private lateinit var adapterSchedulled: AppointmentAdapter
    private lateinit var adapterComplete: AppointmentAdapter
    private lateinit var adapterCancel: AppointmentAdapter

    private lateinit var tvNoScheduledAppointments: TextView
    private lateinit var tvNoCompleteAppointments: TextView
    private lateinit var tvNoCanceledAppointments: TextView

    private lateinit var scheduledAppointments: List<Appointment>
    private lateinit var completedAppointments: List<Appointment>
    private lateinit var canceledAppointments: List<Appointment>
    public final data class PatientTable(
        val firstname: String = "",
        val lastname: String = "",
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointments_confirm, container, false)

        recyclerView = view.findViewById(R.id.rvappointmentsupdated)
        recyclerViewComplete = view.findViewById(R.id.rvCompletedAppointments)
        recyclerViewCancel = view.findViewById(R.id.rvCancelledAppointments)

        tvNoScheduledAppointments = view.findViewById(R.id.tvNoScheduledAppointments)
        tvNoCompleteAppointments = view.findViewById(R.id.tvNoCompleteAppointments)
        tvNoCanceledAppointments = view.findViewById(R.id.tvNoCanceledAppointments)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewComplete.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCancel.layoutManager = LinearLayoutManager(requireContext())

        //val query = FirebaseDatabase.getInstance().reference.child("appointments").limitToLast(100)

        val queryScheduled = FirebaseDatabase.getInstance().reference
            .child("appointments")
            .orderByChild("status")
            .equalTo("Scheduled")

        val options = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(queryScheduled, Appointment::class.java)
            .build()

        adapterSchedulled = AppointmentAdapter(options)

        adapterSchedulled.startListening()

        queryScheduled.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount == 0L) {
                    tvNoScheduledAppointments.visibility = View.VISIBLE
                } else {
                    tvNoScheduledAppointments.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        recyclerView.adapter = adapterSchedulled
//        -----------------------------------------------------------
        val queryCompleted = FirebaseDatabase.getInstance().reference
            .child("appointments")
            .orderByChild("status")
            .equalTo("Completed")

        val optionsComplete = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(queryCompleted, Appointment::class.java)
            .build()

        adapterComplete = AppointmentAdapter(optionsComplete)

        adapterComplete.startListening()

        queryCompleted.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount == 0L) {
                    tvNoCompleteAppointments.visibility = View.VISIBLE
                } else {
                    tvNoCompleteAppointments.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
        recyclerViewComplete.adapter = adapterComplete

//        -----------------------------------------------------------


        val queryCancelled = FirebaseDatabase.getInstance().reference
            .child("appointments")
            .orderByChild("status")
            .equalTo("Cancelled")

        val optionsCancel = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(queryCancelled, Appointment::class.java)
            .build()

        adapterCancel = AppointmentAdapter(optionsCancel)
        adapterCancel.startListening()

        queryCancelled.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount == 0L) {
                    tvNoCanceledAppointments.visibility = View.VISIBLE
                } else {
                    tvNoCanceledAppointments.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
        recyclerViewCancel.adapter = adapterCancel

        return view
    }

    override fun onStart() {
        super.onStart()
        adapterSchedulled.startListening()
        adapterComplete.startListening()
        adapterCancel.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterSchedulled.stopListening()
        adapterComplete.stopListening()
        adapterCancel.stopListening()
    }

}

