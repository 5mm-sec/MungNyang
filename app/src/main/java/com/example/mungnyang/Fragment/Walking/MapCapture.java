package com.example.mungnyang.Fragment.Walking;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLException;
import android.os.Environment;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.vectormap.MapLogger;
import com.kakao.vectormap.graphics.gl.GLSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public class MapCapture {
    // Firebase Storage 참조를 가져옴
    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface OnCaptureListener {
        void onCaptured(boolean isSucceed, String fileName, String imageUrl);

    }

    public static void capture(Activity activity, GLSurfaceView surfaceView, OnCaptureListener listener) {
        String fileName = "MapCapture_" + System.currentTimeMillis() + ".png";

        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                EGL10 egl = (EGL10) EGLContext.getEGL();
                GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
                Bitmap bitmap = createBitmapFromGLSurface(0, 0, surfaceView.getWidth(),
                        surfaceView.getHeight(), gl);

                boolean isSucceed = bitmapToImage(activity.getApplicationContext(), bitmap, fileName);


                if (isSucceed) {
                    // 이미지 업로드를 수행하고 다운로드 URL을 얻음
                    uploadImageToFirebaseStorage(activity, fileName, new OnImageUploadListener() {
                        @Override
                        public void onImageUploaded(boolean isSucceed, String imageUrl) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onCaptured(isSucceed,fileName, imageUrl);
                                }
                            });
                        }
                    });
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCaptured(false, null,null);
                        }
                    });
                }
            }
        });
    }


    private static Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl)
            throws OutOfMemoryError {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;

            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;

                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    private static boolean bitmapToImage(Context context, Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            return false;
        }

        // 앱 전용 디렉토리 경로 설정
        File directory = context.getExternalFilesDir("MapCaptureDemo");

        // 디렉토리가 존재하지 않으면 생성
        if (directory == null || !directory.exists()) {
            if (!directory.mkdirs()) {
                MapLogger.e("Failed to create directory");
                return false;
            }
        }

        File tempFile = new File(directory, fileName);

        try {
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            MapLogger.e("FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            MapLogger.e("IOException : " + e.getMessage());
        }
        return false;
    }

    // Firebase Storage에 이미지를 업로드하는 함수
    private static void uploadImageToFirebaseStorage(Context context, String fileName, OnImageUploadListener listener) {
        // Storage 참조 생성
        StorageReference storageRef = storage.getReference();

        // 업로드할 이미지 파일 경로 설정
        File directory = context.getExternalFilesDir("MapCaptureDemo");
        if (directory != null) {
            String filePath = directory.getAbsolutePath() + File.separator + fileName;

            // Storage에 이미지 업로드
            StorageReference imageRef = storageRef.child("mapImage/" + fileName);
            UploadTask uploadTask = imageRef.putFile(Uri.fromFile(new File(filePath)));

            // 업로드 리스너 설정
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // 업로드 성공 시 이미지의 다운로드 URL을 가져옴
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    listener.onImageUploaded(true, imageUrl);
                });
            }).addOnFailureListener(e -> {
                // 업로드 실패 시 오류 처리
                Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                listener.onImageUploaded(false, null);
            });
        } else {
            // 디렉토리가 null이라면 처리할 수 없음
            listener.onImageUploaded(false, null);
        }
    }

    // 이미지 업로드 결과를 처리하는 리스너 인터페이스
    public interface OnImageUploadListener {
        void onImageUploaded(boolean isSucceed, String imageUrl);
    }
}