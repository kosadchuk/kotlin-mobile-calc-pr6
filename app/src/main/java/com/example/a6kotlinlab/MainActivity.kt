package com.example.a6kotlinlab

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a6kotlinlab.ui.theme._6KotlinLabTheme
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _6KotlinLabTheme {
                ElectricalLoadCalculator()
            }
        }
    }
}

data class ElectricalAppliance(
    var Nn: Double,
    var CosPhi: Double,
    var Un: Double,
    var EAn: Double,
    var Pn: Double,
    var Kv: Double,
    var tgphi: Double,

    var productOfEA: Double = 0.0,
    var Ir: Double = 0.0,
)

val KrTable = mapOf(
    1 to listOf(8.0, 5.33, 4.0, 2.67, 2.0, 1.6, 1.33, 1.14, 1.0),
    2 to listOf(6.22, 4.33, 3.39, 2.45, 1.98, 1.6, 1.33, 1.14, 1.0),
    3 to listOf(4.06, 2.89, 2.31, 1.74, 1.45, 1.34, 1.22, 1.14, 1.0),
    4 to listOf(3.24, 2.35, 1.91, 1.47, 1.25, 1.21, 1.12, 1.06, 1.0),
    5 to listOf(2.84, 2.09, 1.72, 1.35, 1.16, 1.1, 1.06, 1.01, 1.0),
    6 to listOf(2.64, 1.96, 1.62, 1.28, 1.14, 1.08, 1.03, 1.01, 1.0),
    7 to listOf(2.49, 1.86, 1.54, 1.23, 1.12, 1.1, 1.04, 1.0, 1.0),
    8 to listOf(2.37, 1.78, 1.48, 1.19, 1.1, 1.08, 1.03, 1.0, 1.0),
    9 to listOf(2.27, 1.71, 1.43, 1.16, 1.09, 1.07, 1.01, 1.0, 1.0),
    10 to listOf(2.18, 1.65, 1.39, 1.13, 1.07, 1.05, 1.0, 1.0, 1.0),
    12 to listOf(2.04, 1.56, 1.32, 1.08, 1.05, 1.03, 1.0, 1.0, 1.0),
    14 to listOf(1.94, 1.49, 1.27, 1.05, 1.02, 1.0, 1.0, 1.0, 1.0),
    16 to listOf(1.85, 1.43, 1.23, 1.02, 1.0, 1.0, 1.0, 1.0, 1.0),
    18 to listOf(1.78, 1.39, 1.19, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    20 to listOf(1.72, 1.35, 1.16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    25 to listOf(1.6, 1.27, 1.1, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    30 to listOf(1.51, 1.21, 1.05, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    35 to listOf(1.44, 1.16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    40 to listOf(1.4, 1.13, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    50 to listOf(1.3, 1.07, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    60 to listOf(1.25, 1.03, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    80 to listOf(1.16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    100 to listOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
)

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectricalLoadCalculator() {
    val electricalAppliancesData = remember {
        mutableStateMapOf(
            "Шліфувальний верстат (1-4)" to ElectricalAppliance(0.92, 0.9, 0.38, 4.0, 20.0, 0.15, 1.33),
            "Свердлильний верстат (5-6)" to ElectricalAppliance(0.92, 0.9, 0.38, 2.0, 14.0, 0.12, 1.0),
            "Фугувальний верстат (9-12)" to ElectricalAppliance(0.92, 0.9, 0.38, 4.0, 42.0, 0.15, 1.33),
            "Циркулярна пила (13)" to       ElectricalAppliance(0.92, 0.9, 0.38, 1.0, 36.0, 0.3, 1.52),
            "Прес (16)" to                  ElectricalAppliance(0.92, 0.9, 0.38, 1.0, 20.0, 0.5, 0.75),
            "Полірувальний верстат (24)" to ElectricalAppliance(0.92, 0.9, 0.38, 1.0, 40.0, 0.2, 1.0),
            "Фрезерний верстат (26-27)" to  ElectricalAppliance(0.92, 0.9, 0.38, 2.0, 32.0, 0.2, 1.0),
            "Вентилятор (36)" to            ElectricalAppliance(0.92, 0.9, 0.38, 1.0, 20.0, 0.65, 0.75),
        )
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Шліфувальний верстат (1-4)") }
    val selectedData = electricalAppliancesData[selectedOption]!!

    var result by remember { mutableStateOf<Map<String, Any>?>(null) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Калькулятор розрахунку електричних навантажень обʼєктів")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedOption,
                onValueChange = {},
                label = { Text("Найменування ЕП") },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                electricalAppliancesData.keys.forEach { name ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            selectedOption = name
                            expanded = false
                        }
                    )
                }
            }
        }


        TextField(
            value = selectedData.Nn.toString(),
            onValueChange = {
                val newNn = it.toDoubleOrNull() ?: 0.0
                electricalAppliancesData[selectedOption] = selectedData.copy(Nn = newNn)
                            },
            label = { Text("Номінальне значення коефіцієнта корисної дії (Nn)") }
        )
        TextField(
            value = selectedData.CosPhi.toString(),
            onValueChange = {
                val newCosPhi = it.toDoubleOrNull() ?: 0.0
                electricalAppliancesData[selectedOption] = selectedData.copy(CosPhi = newCosPhi)
                            },
            label = { Text("Коефіцієнт потужності (CosPhi)") }
        )
        TextField(
            value = selectedData.Un.toString(),
            onValueChange = {
                val newUn = it.toDoubleOrNull() ?: 0.0
                electricalAppliancesData[selectedOption] = selectedData.copy(Un = newUn)
                            },
            label = { Text("Напруга (Un, кВ)") }
        )
        TextField(
            value = selectedData.EAn.toString(),
            onValueChange = {
                val newEAn = it.toDoubleOrNull() ?: 0.0
                electricalAppliancesData[selectedOption] = selectedData.copy(EAn = newEAn)
                            },
            label = { Text("Кількість ЕП (EAn)") }
        )
        TextField(
            value = selectedData.Pn.toString(),
            onValueChange = {
                val newPn = it.toDoubleOrNull() ?: 0.0
                electricalAppliancesData[selectedOption] = selectedData.copy(Pn = newPn)
                            },
            label = { Text("Номінальна потужність (Pn)") }
        )
        TextField(
            value = selectedData.Kv.toString(),
            onValueChange = {
                val newKv = it.toDoubleOrNull() ?: 0.0
                electricalAppliancesData[selectedOption] = selectedData.copy(Kv = newKv)
                            },
            label = { Text("Коефіцієнт використання (Kv)") }
        )
        TextField(
            value = selectedData.tgphi.toString(),
            onValueChange = {
                val newtgphi = it.toDoubleOrNull() ?: 0.0
                electricalAppliancesData[selectedOption] = selectedData.copy(tgphi = newtgphi)
                            },
            label = { Text("tgφ") }
        )

        Button(
            onClick = {
                result = CalculateElectricalLoad(electricalAppliancesData)
            }
        ) {
            Text(text = "Розрахувати")
        }

        result?.let { map ->
            if (map.isNotEmpty()) {
                val Kv = map["Kv"]
                val ne = map["ne"]
                val Kr = map["Kr"]
                val Pr = map["Pr"]
                val Qr = map["Qr"]
                val Sr = map["Sr"]
                val Ir = map["Ir"]

                Text(text = "1.1. Груповий коефіцієнт використання для ШР1=ШР2=ШР3: $Kv")
                Text(text = "1.2. Ефективна кількість ЕП для ШР1=ШР2=ШР3: $ne")
                Text(text = "1.3. Розрахунковий коефіцієнт активної потужності для ШР1=ШР2=ШР3: $Kr")
                Text(text = "1.4. Розрахункове активне навантаження для ШР1=ШР2=ШР3: $Pr кВт")
                Text(text = "1.5. Розрахункове реактивне навантаження для ШР1=ШР2=ШР3: $Qr квар.")
                Text(text = "1.6. Повна потужність для ШР1=ШР2=ШР3: $Sr кВ*А")
                Text(text = "1.7. Розрахунковий груповий струм для ШР1=ШР2=ШР3: $Ir А")
            }
        }

    }
}

fun Double.roundTo(n: Int): Double {
    val factor = 10.0.pow(n)
    return Math.round(this * factor) / factor
}

fun CalculateElectricalLoad(electricalAppliances: Map<String, ElectricalAppliance>): Map<String, Any> {

    electricalAppliances.forEach { (name, appliance) ->
        // 3. Визначимо розрахункові струми на І рівні електропостачання всіх однакових ЕП
        //3.1. Знаходимо добуток і заносимо у відповідні комірки таблиці 6.6:
        appliance.productOfEA = appliance.EAn * appliance.Pn

        //3.2. Знаходимо розрахунковий струм ЕП1 і заносимо у відповідні комірки таблиці 6.6:
        appliance.Ir = appliance.productOfEA / (sqrt(3.0) * appliance.Un * appliance.CosPhi * appliance.Nn) // A
    }

    // 4. Визначимо розрахункові групові навантаження РП або ШР визначаються в наступній
    //послідовності. Наприклад для ШР1 матимемо наступну послідовність:

    //4.1. Знаходимо груповий коефіцієнт використання:
    var numerator = electricalAppliances.values.sumOf { appliance ->
        (appliance.EAn * appliance.Pn * appliance.Kv).roundTo(4)
    }
    var denominator = electricalAppliances.values.sumOf { appliance ->
        (appliance.EAn * appliance.Pn).roundTo(4)
    }
    val Kv = (numerator / denominator).roundTo(4)

    //4.2. Знаходимо ефективну кількість ЕП:
    numerator = electricalAppliances.values.sumOf { appliance ->
        (appliance.EAn * appliance.Pn).roundTo(4)
    }
    denominator = electricalAppliances.values.sumOf { appliance ->
        (appliance.EAn * appliance.Pn.pow(2.0)).roundTo(4)
    }
    val ne = (numerator.pow(2.0) / denominator).toInt()

    //4.3. Знаходимо розрахунковий коефіцієнт активної потужності по таблиці 6.3
    val Kr = calculateAKr(ne, Kv.roundTo(2))

    //4.4. Знаходимо розрахункове активне навантаження:
    var PnSum = electricalAppliances.values.sumOf { appliance ->
        (appliance.EAn * appliance.Pn).roundTo(2)
    }
    val Pr = (Kr * Kv * PnSum).roundTo(2)

    //4.5. Знаходимо розрахункове реактивне навантаження:
    var tgPhiSum = calculateTgPhi(electricalAppliances)
    val Qr = 1.0 * Kv.roundTo(1) * PnSum.roundTo(2) * tgPhiSum.roundTo(2)

    //4.6. Знаходимо повну потужність:
    val Sr = sqrt((Pr.pow(2.0) + Qr.pow(2.0)))

    //4.7. Знаходимо розрахунковий груповий струм ШР1:
    val Ir = Pr / 0.38



    return mapOf(
        "Kv" to Kv.roundTo(4),
        "ne" to ne,
        "Kr" to Kr,
        "Pr" to Pr,
        "Qr" to Qr.roundTo(3),
        "Sr" to Sr.roundTo(4),
        "Ir" to Ir.roundTo(2),
    )
}

fun calculateAKr(ne: Int, Kv: Double): Double {
    val closestNe = KrTable.keys.minByOrNull { Math.abs(it - ne) }

    val row = KrTable[closestNe]

    val KvColumns = listOf(0.10, 0.15, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80)

    val columnIndex = if (Kv > KvColumns.last()) {
        KvColumns.lastIndex
    } else {
        KvColumns.indexOfLast { it <= Kv }
    }
    return row?.getOrNull(columnIndex) ?: 1.0
}

fun calculateTgPhi(appliances: Map<String, ElectricalAppliance>): Double {
    val numerator = appliances.values.sumOf { appliance ->
        (appliance.EAn * appliance.Pn * appliance.tgphi).roundTo(4)
    }
    val denominator = appliances.values.sumOf { appliance ->
        (appliance.EAn * appliance.Pn).roundTo(4)
    }
    return numerator / denominator
}
