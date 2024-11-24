package com.sprtcoding.safeako.model

data class AssessmentQuestion(
    var gender: Gender,
    var relationships: Relationships,
    var education: Education,
    var interest: Interest,
    var birthMother: BirthMother,
    var sexualHistory: SexualHistory,
    var sexual2: Sexual2,
    var sexual3: Sexual3,
    var sexual4: Sexual4,
    var sexual5: Sexual5,
    var sexual6: Sexual6,
    var sexual7: Sexual7,
    var reason: Reason,
    var medicalHistory: MedicalHistory
)

data class Gender(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null
)

data class Relationships(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null
)

data class Education(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null,
    var a4: String? = null,
    var a5: String? = null,
    var a6: String? = null,
    var a7: String? = null
)

data class Interest(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null,
    var a4: String? = null
)

data class BirthMother(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null
)

data class SexualHistory(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null
)

data class Sexual2(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null
)

data class Sexual3(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null
)

data class Sexual4(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null
)

data class Sexual5(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null
)

data class Sexual6(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null
)

data class Sexual7(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null
)

data class Reason(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null,
    var a4: String? = null,
    var a5: String? = null
)

data class MedicalHistory(
    var q: String? = null,
    var a1: String? = null,
    var a2: String? = null,
    var a3: String? = null,
    var a4: String? = null,
    var a5: String? = null
)
