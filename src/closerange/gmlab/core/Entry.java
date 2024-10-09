package closerange.gmlab.core;

import closerange.display.Display;
import closerange.display.DisplayLoop;
import closerange.display.ImGuiBinder;

public class Entry {
    public static void main(String[] args) {
        ImGuiBinder.showDearImGui = false;
        ImGuiBinder.enableDocker = false;

        ImGuiBinder.addFont("Roboto", 30, "res/fonts/Roboto-Light.ttf");
        Display display = new Display(
                "AB Testing", // title
                1920 / 3, // width
                1080 / 3, // height
                false, // Will start in windowed mode
                new DisplayLoop() { // Usually better to just create a new class
                    @Override
                    public void onTick(float dt) {
                    }

                    // Called when the display is started
                    @Override
                    public void onStart() {
                        new MainFrame();
                    }

                    // Called when the display is closed
                    @Override
                    public void onClose() {
                    }

                    // Called when a file is dropped onto the display
                    @Override
                    public void onFileDrop(String path) {
                    }
                });
        display.setIcon("res/ABLogo.png");
        display.start();
    }
}
