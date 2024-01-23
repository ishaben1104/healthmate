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

        val sharedPreferences = requireContext().getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val patientId = sharedPreferences.getString("loginid", "").toString()

        // Create a reference to the appointments node
        val appointmentsRef = FirebaseDatabase.getInstance().reference.child("appointments").orderByChild("patientId").equalTo(patientId)

        // Create a listener for all appointments for the patient
        val allAppointmentsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allAppointments = mutableListOf<Appointment>()
//                val scheduledAppointments = mutableListOf<Appointment>()
                scheduledAppointments = mutableListOf()
                completedAppointments = mutableListOf()
                canceledAppointments = mutableListOf()

                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    if (appointment != null) {
                        allAppointments.add(appointment)

                        // Filter appointments based on status
                        when (appointment.status) {
                            "Scheduled" -> (scheduledAppointments as MutableList<Appointment>).add(appointment)
                            "Completed" -> (completedAppointments as MutableList<Appointment>).add(appointment)
                            "Canceled" -> (canceledAppointments as MutableList<Appointment>).add(appointment)
                        }
                    }
                }

                // Update visibility based on filtered lists
                tvNoScheduledAppointments.visibility = if (scheduledAppointments.isEmpty()) View.VISIBLE else View.INVISIBLE
                tvNoCompleteAppointments.visibility = if (completedAppointments.isEmpty()) View.VISIBLE else View.INVISIBLE
                tvNoCanceledAppointments.visibility = if (canceledAppointments.isEmpty()) View.VISIBLE else View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        }

        // Attach the listener to the appointments reference
        appointmentsRef.addListenerForSingleValueEvent(allAppointmentsListener)

        // Create FirebaseRecyclerOptions and adapters for each status
        val optionsScheduled = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(appointmentsRef, Appointment::class.java)
            .build()
        adapterSchedulled = AppointmentAdapter(optionsScheduled)
        recyclerView.adapter = adapterSchedulled

        val optionsCompleted = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(appointmentsRef, Appointment::class.java)
            .build()
        adapterComplete = AppointmentAdapter(optionsCompleted)
        recyclerViewComplete.adapter = adapterComplete

        val optionsCanceled = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(appointmentsRef, Appointment::class.java)
            .build()
        adapterCancel = AppointmentAdapter(optionsCanceled)
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

