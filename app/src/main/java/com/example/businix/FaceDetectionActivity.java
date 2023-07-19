//package com.example.businix;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.OptIn;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.ImageProxy;
//import androidx.camera.core.Preview;
//import androidx.camera.view.PreviewView;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.util.Size;
//import android.widget.Button;
//import android.Manifest;
//import android.widget.Toast;
//
//import androidx.camera.lifecycle.ProcessCameraProvider;
//
//import com.google.common.util.concurrent.ListenableFuture;
//import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.face.Face;
//import com.google.mlkit.vision.face.FaceDetection;
//import com.google.mlkit.vision.face.FaceDetector;
//import com.google.mlkit.vision.face.FaceLandmark;
//
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Point;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.concurrent.ExecutionException;
//
//public class FaceDetectionActivity extends AppCompatActivity {
//    private Button btnShowCam;
//    private PreviewView previewView;
//    private final static int REQUEST_IMAGE_CAPTURE = 124;
//    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
//    private ProcessCameraProvider cameraProvider;
//    private Camera camera;
//
//    private ImageAnalysis imageAnalysis;
//
//    private BaseLoaderCallback loaderCallback;
//    private CascadeClassifier faceCascade;
//    private Mat sampleImage;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_face_detection);
//
//        btnShowCam = (Button) findViewById(R.id.btn_show_cam);
//        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//        previewView = (PreviewView) findViewById(R.id.previewView);
//
//        btnShowCam.setOnClickListener(v -> {
//            openCamera();
//        });
//
//        loaderCallback = new BaseLoaderCallback(this) {
//            @Override
//            public void onManagerConnected(int status) {
//                switch (status) {
//                    case LoaderCallbackInterface.SUCCESS:
//                        // Sao chép file xml haarcascade_frontalface_default.xml từ thư mục assets vào bộ nhớ trong của ứng dụng
//                        copyHaarCascadeFile();
//                        // Khởi tạo CascadeClassifier
//                        faceCascade = new CascadeClassifier(faceDataFile.getAbsolutePath());
//                        break;
//                    default:
//                        super.onManagerConnected(status);
//                        break;
//                }
//            }
//        };
//        OpenCVLoader.initDebug();
//    }
//
//    private void openCamera() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);
//        } else {
//            startCamera();
//        }
//    }
//
//    private void startCamera() {
//        // Sử dụng cameraProviderFuture để khởi tạo ProcessCameraProvider
//        cameraProviderFuture.addListener(() -> {
//            try {
//                cameraProvider = cameraProviderFuture.get();
//                camera = bindPreview(cameraProvider);
//                startFaceDetection();
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
//
//    private Camera bindPreview(ProcessCameraProvider cameraProvider) {
//        Preview preview = new Preview.Builder().build();
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
//                .build();
//
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//        try {
//            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
//            return camera;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to start camera", Toast.LENGTH_SHORT).show();
//        }
//        return null;
//    }
//
//    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
//    private boolean isImageValid(ImageProxy imageProxy) {
//        return imageProxy != null && imageProxy.getImage() != null;
//    }
//
//    private void startFaceDetection() {
//        // Khởi tạo ImageAnalysis với kích thước hình ảnh thích hợp
//
//        imageAnalysis = new ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build();
//
//        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
//            @Override
//            public void analyze(@NonNull ImageProxy image) {
//                detectAndSaveFace(image);
//            }
//        });
//    }
//    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
//    private void detectAndSaveFace(ImageProxy imageProxy) {
//        if (!isImageValid(imageProxy)) {
//            Log.e("MyTAG", "Image is invalid or already closed.");
//            return;
//        }
//        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
//        FaceDetector detector = FaceDetection.getClient();
//
//        detector.process(image)
//                .addOnSuccessListener(faces -> {
//                    // Xử lý kết quả nhận dạng khuôn mặt ở đây
//
//                    if (faces.size() > 0) {
//                        // Đã nhận dạng được khuôn mặt, bạn có thể lưu thông tin khuôn mặt vào Firestore ở đây
//                        // Ví dụ:
//                        for (Face face : faces) {
//                            FaceLandmark leftEye = face.getLandmark(FaceLandmark.LEFT_EYE);
//                            FaceLandmark rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE);
//                            // Lưu các thông tin về khuôn mặt vào Firestore tại đây
//                        }
//
//                        // Hiển thị thông báo cho người dùng
//                        Toast.makeText(this, "okroine", Toast.LENGTH_LONG).show();
//                        Log.i("MyTAG", "OKroine");
//                    } else {
//                        // Không nhận dạng được khuôn mặt, tiếp tục để người dùng lấy khuôn mặt
//                    }
//
//                    // Đóng imageProxy sau khi đã xử lý xong
//                    imageProxy.close();
//                })
//                .addOnFailureListener(e -> {
//                    // Xử lý khi nhận dạng khuôn mặt thất bại
//                    e.printStackTrace();
//                    Toast.makeText(this, "Failed to detect face", Toast.LENGTH_SHORT).show();
//                    // Đóng imageProxy nếu xử lý thất bại để giải phóng tài nguyên
//                    imageProxy.close();
//                });
//    }
//
//
//
//    private void closeCameraAndImageAnalysis() {
//        if (cameraProvider != null) {
//            if (camera != null) {
//                cameraProvider.unbindAll();
//                camera = null;
//            }
//        }
//        if (imageAnalysis != null) {
//            imageAnalysis.clearAnalyzer();
//            imageAnalysis = null;
//        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        closeCameraAndImageAnalysis();
//    }
//
//    private void copyHaarCascadeFile() {
//        try {
//            InputStream is = getAssets().open("haarcascade_frontalface_default.xml");
//            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//            File faceDataFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");
//            FileOutputStream os = new FileOutputStream(faceDataFile);
//
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//            while ((bytesRead = is.read(buffer)) != -1) {
//                os.write(buffer, 0, bytesRead);
//            }
//
//            is.close();
//            os.close();
//            faceCascade = new CascadeClassifier(faceDataFile.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}