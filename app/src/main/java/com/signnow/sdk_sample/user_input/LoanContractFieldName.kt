package com.signnow.sdk_sample.user_input

sealed class LoanContractFieldName(val value: String) {
    object FirstName: LoanContractFieldName("first_name")
    object SecondName: LoanContractFieldName("second_name")
    object Age: LoanContractFieldName("age")
    object Amount: LoanContractFieldName("amount")
}
