    package com.example.healthmate

    import android.content.Context
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ListView
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.firebase.ui.database.FirebaseRecyclerOptions
    import com.google.firebase.database.FirebaseDatabase

    class FragmentAppointments : Fragment(), AppointmentListAdapter.OnItemClickListener {

        private lateinit var recyclerView: RecyclerView
        private lateinit var noPendingMsg: TextView
        private lateinit var adapter: AppointmentListAdapter

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_appointments, container, false)

            recyclerView = view.findViewById(R.id.appointmentsRecyclerView)
            noPendingMsg = view.findViewById(R.id.noPendingAppointmentsMessage)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Your Firebase query
            val query = FirebaseDatabase.getInstance().reference
                .child("appointments")
                .orderByChild("status")
                .equalTo("Scheduled")

            val options = FirebaseRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment::class.java)
                .build()

            adapter = AppointmentListAdapter(options)
            adapter.setOnItemClickListener(this) // Set the listener for button clicks

            recyclerView.adapter = adapter

            //Log.d("AppointmentsCnt", adapter.itemCount.toString())
            if (adapter.itemCount == 0)
            {
                noPendingMsg.visibility = View.VISIBLE
            }
            else {
                noPendingMsg.visibility = View.INVISIBLE
            }
            // Set a listener to check if there are no pending appointments
            adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    checkNoPendingAppointments()
                }
            })

            return view
        }

        // Implement the interface methods
        override fun onApproveClick(position: Int, appointment: Appointment) {
            // Handle approve click (if needed)
            val databaseRef = FirebaseDatabase.getInstance().getReference("appointments")
            val appointmentId = adapter.getRef(position).key

            val sharedPreferences = requireContext().getSharedPreferences("MySession", Context.MODE_PRIVATE)
            val doctorId = sharedPreferences.getString("loginid", "")
            val doctorName = sharedPreferences.getString("fullname", "")

            // Update the status to 'Scheduled'
            databaseRef.child(appointmentId!!).child("status").setValue("Scheduled")
            databaseRef.child(appointmentId!!).child("doctorId").setValue(doctorId)
            databaseRef.child(appointmentId!!).child("doctorName").setValue(doctorName)
        }

        override fun onCancelClick(position: Int, appointment: Appointment) {
            // Handle cancel click (if needed)
            val databaseRef = FirebaseDatabase.getInstance().getReference("appointments")
            val appointmentId = adapter.getRef(position).key

            val sharedPreferences = requireContext().getSharedPreferences("MySession", Context.MODE_PRIVATE)
            val doctorId = sharedPreferences.getString("loginid", "")
            val doctorName = sharedPreferences.getString("fullname", "")

            // Update the status to 'Cancelled'
            databaseRef.child(appointmentId!!).child("status").setValue("Cancelled")
            databaseRef.child(appointmentId!!).child("doctorId").setValue(doctorId)
            databaseRef.child(appointmentId!!).child("doctorName").setValue(doctorName)
        }

        override fun onStart() {
            super.onStart()
            adapter.startListening()
        }

        override fun onStop() {
            super.onStop()
            adapter.stopListening()
        }
        // Function to check if there are no pending appointments
        private fun checkNoPendingAppointments() {
            val noPendingAppointmentsMessage = view?.findViewById<TextView>(R.id.noPendingAppointmentsMessage)

            if (adapter.itemCount == 0) {
                // Show the message when there are no pending appointments
                noPendingAppointmentsMessage?.visibility = View.VISIBLE
            } else {
                // Hide the message when there are pending appointments
                noPendingAppointmentsMessage?.visibility = View.GONE
            }
        }
    }