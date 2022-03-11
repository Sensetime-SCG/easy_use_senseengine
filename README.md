# SenseEngine Uvc Camera Stream Display Guide


update: 2022.03.12 / ChenYang


## Steps

1. Add `TextureView` in your layout as camera stream display. Also you can self-implementation `TextureView` like `com.example.easy.ui.CameraTextureView` in this demo code.
2. Bind layout in your activity and initialize the `TextureView`.
3. Initialize `UsbMonitor` and register it.
4. Bind frame callback to `TextureView` 
