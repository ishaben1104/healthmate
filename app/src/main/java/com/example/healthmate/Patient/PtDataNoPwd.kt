package com.example.healthmate.Patient

data class PtDataNoPwd(
    var userId: String? = "",
                           var firstname: String? = "",
                           var lastname: String? = "",
                           var phone: String? = "",
                           var email: String? = "",
                           var username: String? = "",
                           var dob: String? = ""
) {
    // Empty constructor required for Firebase deserialization
    constructor() : this("", "", "", "", "", "","")
}

