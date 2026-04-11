package com.example.happyj.export

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.happyj.data.ReportesResponse
import com.example.happyj.data.ResumenGeneral
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Genera un PDF con totales de ventas del [ReportesResponse] (misma información que la pantalla de estadísticas).
 */
object VentasReportePdfWriter {

    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 48f
    private const val BOTTOM_SAFE = 56f

    fun write(
        reporte: ReportesResponse,
        periodoClave: String,
        out: OutputStream,
    ) {
        val doc = PdfDocument()
        val session = PdfSession(doc)
        try {
            val tituloPeriodo = etiquetaPeriodoLargo(periodoClave)
            val generado = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-ES")),
            )

            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = Color.BLACK
            }
            val headingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 13f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = Color.BLACK
            }
            val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 11f
                color = Color.DKGRAY
            }
            val smallPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 9.5f
                color = Color.GRAY
            }

            session.append("Happy Jump — Reporte de ventas", titlePaint)
            session.append(tituloPeriodo, headingPaint)
            session.append(
                "Rango: ${reporte.rango.inicio} → ${reporte.rango.fin}",
                bodyPaint,
            )
            session.append("Generado: $generado", smallPaint)
            session.gap(14f)

            val res = resumenDesde(reporte)
            val comp = reporte.comparacionIngresos

            session.append("Totales del período", headingPaint)
            session.append(
                "Ingresos totales: S/ ${formatSoles(res.ingresosTotales)}",
                bodyPaint,
            )
            if (comp != null) {
                session.append(
                    "  · Cancha: S/ ${formatSoles(comp.cancha)}",
                    bodyPaint,
                )
                session.append(
                    "  · Salones: S/ ${formatSoles(comp.salones)}",
                    bodyPaint,
                )
            }
            session.append(
                "Total adelantos cobrados (periodo): S/ ${formatSoles(res.totalAdelantos)}",
                bodyPaint,
            )
            session.append(
                "Reservas cancha: ${res.totalReservasCancha}   |   Reservas salones: ${res.totalReservasSalones}",
                bodyPaint,
            )
            session.gap(12f)

            session.append("Cancha", headingPaint)
            session.append(
                "Ingresos en el período: S/ ${formatSoles(reporte.cancha.ingresosPeriodo)}",
                bodyPaint,
            )
            session.append(
                "Referencia semanal (cancha): S/ ${formatSoles(reporte.cancha.ingresosSemanales)}",
                smallPaint,
            )
            session.append(
                "Referencia mensual (cancha, mes calendario actual): S/ ${formatSoles(reporte.cancha.ingresosMensuales)}",
                smallPaint,
            )
            session.append("Reservas en el período: ${reporte.cancha.totalReservas}", bodyPaint)
            if (reporte.cancha.horariosMasOcupados.isNotEmpty()) {
                session.append("Horarios con más reservas (periodo):", bodyPaint)
                reporte.cancha.horariosMasOcupados.take(8).forEach { h ->
                    session.append(
                        "  · ${h.hora} — ${h.reservas} reserva(s)",
                        bodyPaint,
                    )
                }
            }
            session.gap(12f)

            session.append("Salones", headingPaint)
            session.append(
                "Ingresos totales en el período: S/ ${formatSoles(reporte.salones.ingresosTotalPeriodo)}",
                bodyPaint,
            )
            if (reporte.salones.porSalon.isNotEmpty()) {
                session.append("Por salón:", bodyPaint)
                reporte.salones.porSalon.forEach { row ->
                    session.append(
                        "  · ${row.salon}: S/ ${formatSoles(row.ingresos)} (${row.reservas} reservas)",
                        bodyPaint,
                    )
                }
            }
            reporte.salones.salonMasAlquilado?.let { top ->
                val n = top.n ?: 0
                session.append(
                    "Salón más alquilado: ${top.salon} ($n reservas)",
                    smallPaint,
                )
            }
            reporte.salones.salonMasRentable?.let { top ->
                val t = top.total ?: 0.0
                session.append(
                    "Salón más rentable (ingresos en periodo): ${top.salon} (S/ ${formatSoles(t)})",
                    smallPaint,
                )
            }

            session.gap(18f)
            session.append(
                "Documento generado desde la app Happy Jump. Los importes corresponden a reservas no canceladas en el rango indicado.",
                smallPaint,
            )
        } finally {
            session.finishTo(out)
        }
    }

    private fun resumenDesde(r: ReportesResponse): ResumenGeneral {
        r.resumen?.let { return it }
        return ResumenGeneral(
            ingresosTotales = r.cancha.ingresosPeriodo + r.salones.ingresosTotalPeriodo,
            totalReservasCancha = r.cancha.totalReservas,
            totalReservasSalones = r.salones.porSalon.sumOf { it.reservas },
            totalAdelantos = 0.0,
        )
    }

    private fun etiquetaPeriodoLargo(clave: String): String = when (clave) {
        "diario" -> "Periodo: día (resumen del día seleccionado)"
        "semanal" -> "Periodo: semana (lunes a domingo de la semana de referencia)"
        "mensual" -> "Periodo: mes (mes calendario de la fecha de referencia)"
        else -> "Periodo: $clave"
    }

    private fun formatSoles(v: Double): String =
        String.format(Locale.forLanguageTag("es-PE"), "%,.2f", v)

    private class PdfSession(private val doc: PdfDocument) {
        private var pageNumber = 1
        private var page: PdfDocument.Page? = null
        private var canvas: android.graphics.Canvas? = null
        private var y = MARGIN

        private fun startPage() {
            page?.let { doc.finishPage(it) }
            val info = PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create()
            pageNumber++
            val p = doc.startPage(info)
            page = p
            canvas = p.canvas
            y = MARGIN + 8f
        }

        init {
            startPage()
        }

        fun append(text: String, paint: TextPaint) {
            val c = canvas ?: return
            val width = (PAGE_W - 2 * MARGIN).toInt().coerceAtLeast(80)
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setIncludePad(false)
                .build()
            if (y + layout.height > PAGE_H - BOTTOM_SAFE) {
                startPage()
            }
            val c2 = canvas ?: return
            c2.save()
            c2.translate(MARGIN, y)
            layout.draw(c2)
            c2.restore()
            y += layout.height + 6f
        }

        fun gap(extra: Float) {
            y += extra
        }

        fun finishTo(out: OutputStream) {
            page?.let { doc.finishPage(it) }
            page = null
            canvas = null
            doc.writeTo(out)
            doc.close()
        }
    }
}
