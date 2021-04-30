package be.t_ars.xtouch.ui

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter

fun createBarcode(data: String) =
	MatrixToImageWriter.toBufferedImage(
		MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200)
	)