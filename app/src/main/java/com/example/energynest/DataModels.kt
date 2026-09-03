package com.example.energynest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Maps to 'User' table in Supabase
 */
@Serializable
data class User(
    @SerialName("ic_number") val icNumber: String,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("street") val street: String,
    @SerialName("zip_code") val zipCode: Double,
    @SerialName("city") val city: String,
    @SerialName("state") val state: String,
    @SerialName("password") val password: String,
    @SerialName("account_id") val accountId: String? = null,
    @SerialName("account_status") val accountStatus: String = "Active",
    @SerialName("house_no") val houseNo: String
)

/**
 * Maps to 'Home' table in Supabase
 */
@Serializable
data class HomeStats(
    @SerialName("home_id") val homeId: Int? = null,
    @SerialName("ic_number") val icNumber: String,
    @SerialName("date") val date: String,
    @SerialName("generated_kwh") val generatedKwh: Double,
    @SerialName("stored_energy_pct") val storedEnergyPct: Double,
    @SerialName("stored_energy_kwh") val storedEnergyKwh: Double,
    @SerialName("estimated_usage_duration") val estimatedUsageDuration: Double,
    @SerialName("co2_emission") val co2Emission: Double,
    @SerialName("total_savings") val totalSavings: Double
)

/**
 * Maps to 'Smart_Sell' table in Supabase
 */
@Serializable
data class SmartSellData(
    @SerialName("smart_sell_id") val smartSellId: Int? = null,
    @SerialName("ic_number") val icNumber: String,
    @SerialName("payment_id") val paymentId: Int? = null,
    @SerialName("accumulated_credit") val accumulatedCredit: Double,
    @SerialName("amountkwh") val amountKwh: Double, 
    @SerialName("estimated_bill_credit") val estimatedBillCredit: Double,
    @SerialName("auto_Sell_Enabled") val autoSellEnabled: Boolean
)

/**
 * Maps to 'Electric_usage' table in Supabase
 */
@Serializable
data class ElectricUsage(
    @SerialName("usage_id") val usageId: Int? = null,
    @SerialName("ic_number") val icNumber: String,
    @SerialName("month_label") val monthLabel: String,
    @SerialName("total_energy_kwh") val totalEnergyKwh: Double,
    @SerialName("estimated_cost") val estimatedCost: Double,
    @SerialName("average_daily") val averageDaily: Double,
    @SerialName("co2_emission") val co2Emission: Double,
    @SerialName("ac_percent") val acPercent: Int? = 0,
    @SerialName("lighting_percent") val lightingPercent: Int? = 0,
    @SerialName("equipment_percent") val equipmentPercent: Int? = 0,
    @SerialName("appliance_percent") val appliancePercent: Int? = 0,
    @SerialName("other_percent") val otherPercent: Int? = 0
)

/**
 * Maps to 'Payment' table in Supabase
 */
@Serializable
data class PaymentData(
    @SerialName("payment_id") val paymentId: Int? = null,
    @SerialName("title") val title: String,
    @SerialName("reference_no") val referenceNo: String? = null,
    @SerialName("method") val method: String,
    @SerialName("date") val date: String,
    @SerialName("time") val time: String,
    @SerialName("subtotal") val subtotal: Double,
    @SerialName("sst") val sst: Double,
    @SerialName("amount") val amount: Double,
    @SerialName("status") val status: Boolean? = false
)

/**
 * Maps to 'Cream' table in Supabase
 */
@Serializable
data class CreamData(
    @SerialName("cream_id") val creamId: Int? = null,
    @SerialName("payment_id") val paymentId: Int? = null,
    @SerialName("iseligible") val isEligible: Boolean, 
    @SerialName("estimated_income_min") val estimatedIncomeMin: Double,
    @SerialName("estimated_income_max") val estimatedIncomeMax: Double,
    @SerialName("shading_level") val shadingLevel: String 
)

/**
 * Maps to 'Property' table in Supabase
 */
@Serializable
data class PropertyData(
    @SerialName("ic_number") val icNumber: String,
    @SerialName("cream_id") val creamId: Int,
    @SerialName("property_type") val propertyType: String,
    @SerialName("roofspacesqft") val roofSpaceSqFt: Double 
)

/**
 * Maps to 'Service' table in Supabase
 */
@Serializable
data class ServiceData(
    @SerialName("service_id") val serviceId: Int? = null,
    @SerialName("payment_id") val paymentId: Int? = null,
    @SerialName("is_free") val isFree: Boolean = false,
    @SerialName("type") val type: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("location") val location: String,
    @SerialName("status") val status: String? = "Pending",
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Maps to 'Booking' table in Supabase
 */
@Serializable
data class BookingData(
    @SerialName("booking_id") val bookingId: Int? = null,
    @SerialName("ic_number") val icNumber: String,
    @SerialName("service_id") val serviceId: Int,
    @SerialName("date") val date: String,
    @SerialName("time") val time: String
)

/**
 * Maps to 'Feedback' table in Supabase
 */
@Serializable
data class FeedbackData(
    @SerialName("feedback_id") val feedbackId: Int? = null,
    @SerialName("ic_number") val icNumber: String,
    @SerialName("content") val content: String,
    @SerialName("date") val date: String,
    @SerialName("time") val time: String
)

/**
 * Maps to 'Floor_usage' table in Supabase
 */
@Serializable
data class FloorUsage(
    @SerialName("usage_id") val usage_id: Int? = null,
    @SerialName("ic_number") val ic_number: String,
    @SerialName("floor_name") val floor_name: String,
    @SerialName("energy_kwh") val energy_kwh: Double,
    @SerialName("source") val source: String = "Solar",
    @SerialName("created_at") val created_at: String? = null
)
