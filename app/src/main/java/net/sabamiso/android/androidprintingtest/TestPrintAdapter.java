package net.sabamiso.android.androidprintingtest;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

class TestPrintAdapter extends PrintDocumentAdapter {
    public final String TAG = getClass().getSimpleName();

    Activity activity;
    PrintAttributes print_attr;
    PrintedPdfDocument pdf_document;

    public TestPrintAdapter(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onLayout(
            PrintAttributes oldAttributes,
            PrintAttributes newAttributes,
            CancellationSignal cancellationSignal,
            LayoutResultCallback callback,
            Bundle extras)
    {
        Log.d(TAG, "onLayout() newAttributes = " + newAttributes);

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        print_attr = newAttributes;
        int pages = 1;
        pdf_document = new PrintedPdfDocument(activity, newAttributes);

        PrintDocumentInfo info = new PrintDocumentInfo.Builder("TestPrintAdapter.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pages)
                .build();

        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(
            PageRange[] pages,
            ParcelFileDescriptor destination,
            CancellationSignal cancellationSignal,
            final WriteResultCallback callback)
    {
        Log.d(TAG, "onWrite() : ");

        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                pdf_document.close();
                pdf_document = null;
                Toast.makeText(activity, "onWrite() : cancellationSignal.isCanceled()", Toast.LENGTH_LONG).show();
                callback.onWriteCancelled();
            }
        });

        PdfDocument.Page page = pdf_document.startPage(0);
        Canvas canvas = page.getCanvas();
        draw(canvas);
        pdf_document.finishPage(page);

        try {
            pdf_document.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            pdf_document.close();
            pdf_document = null;
        }

        callback.onWriteFinished(pages);
    }

    void draw(Canvas canvas) {
        Paint p_background = new Paint();
        p_background.setARGB(255, 255, 255, 255);
        p_background.setStyle(Paint.Style.FILL);

        Paint p_line = new Paint();
        p_line.setStyle(Paint.Style.STROKE);
        p_line.setStrokeWidth(3);
        p_line.setARGB(255, 0, 0, 0);
        p_line.setAntiAlias(true);

        Paint p_font = new Paint();
        p_font.setStyle(Paint.Style.FILL);
        p_font.setARGB(255, 0, 0, 0);
        p_font.setAntiAlias(true);
        p_font.setTextSize(32);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        int x0 = 20;
        int y0 = 20;

        int x1 = w - 20;
        int y1 = h - 20;

        canvas.drawRect(new Rect(x0, y0, x1, y1), p_line);

        Rect rect = new Rect(x0, y0, x1, y1);

        canvas.drawLine(x0, y0, x1, y1, p_line);
        canvas.drawLine(x1, y0, x0, y1, p_line);

        canvas.drawText("ここに印刷の", 100, 50, p_font);
        canvas.drawText("プレビューが表示されます", 100, 100, p_font);
    }
}
