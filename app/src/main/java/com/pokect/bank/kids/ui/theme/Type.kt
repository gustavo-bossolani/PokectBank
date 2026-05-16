package com.pokect.bank.kids.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pokect.bank.R

// ============================================================================
// Nunito Font Family (6 weights bundled in res/font/)
// Weight 400=Regular, 500=Medium, 600=SemiBold, 700=Bold, 800=ExtraBold, 900=Black
// ============================================================================
val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),       // 400
    Font(R.font.nunito_medium, FontWeight.Medium),         // 500
    Font(R.font.nunito_semibold, FontWeight.SemiBold),     // 600
    Font(R.font.nunito_bold, FontWeight.Bold),             // 700
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),   // 800
    Font(R.font.nunito_black, FontWeight.Black),           // 900
)

// ============================================================================
// Material 3 Typography
// Line heights match UI-SPEC typography table:
//   Body 14sp/1.5 (21sp), BodyLarge 18sp/1.5 (27sp)
//   Label 14sp/1.4 (20sp), Heading 22sp/1.2 (26sp), Display 32sp/1.1 (35sp)
// ============================================================================
val PokectBankTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Black,     // 900
        fontSize = 32.sp,
        lineHeight = 35.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,  // 800
        fontSize = 28.sp,
        lineHeight = 32.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,  // 800
        fontSize = 24.sp,
        lineHeight = 28.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,  // 800
        fontSize = 22.sp,
        lineHeight = 26.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,       // 700
        fontSize = 20.sp,
        lineHeight = 24.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,       // 700
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Normal,     // 400
        fontSize = 18.sp,
        lineHeight = 27.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Normal,     // 400
        fontSize = 14.sp,
        lineHeight = 21.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Normal,     // 400
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Medium,     // 500
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Medium,     // 500
        fontSize = 10.sp,
        lineHeight = 14.sp,
    ),
)
