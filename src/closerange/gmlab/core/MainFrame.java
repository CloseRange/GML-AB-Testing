package closerange.gmlab.core;

import closerange.display.GuiFrame;
import imgui.ImGui;
import imgui.type.ImString;

import java.io.*;

public class MainFrame extends GuiFrame {

    // Runner Link
    // Temp Link
    // Project Name
    // cmd arguments
    private ImString runnerLink = new ImString(125);
    private ImString tempLink = new ImString(125);;
    private ImString projectName = new ImString(125);
    private ImString cmdArgs = new ImString(125);

    private String error = "";

    public MainFrame() {
        super("Frame");
        setVisible(false);
        loadMeta();
    }

    @Override
    public void onTick() {
        title("GML AB Testing");
        inputText("Runner Link", runnerLink);
        inputText("Temp Link", tempLink);
        inputText("Project Name", projectName);
        inputText("Arguments", cmdArgs);
        ImGui.newLine();
        if (button("Execute")) {
            try {
                error = "";
                execute();
                saveMeta();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ImGui.textColored(200, 25, 25, 255, error);
    }

    private void loadMeta() {
        File f = new File("res/meta.txt");
        if (!f.exists()) {
            return;
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("res/meta.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length > 1) {
                    switch (parts[0]) {
                        case "runner":
                            runnerLink.set(parts[1]);
                            break;
                        case "temp":
                            tempLink.set(parts[1]);
                            break;
                        case "project":
                            projectName.set(parts[1]);
                            break;
                        case "args":
                            cmdArgs.set(parts[1]);
                            break;
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
        }

    }

    private void saveMeta() {
        StringBuilder builder = new StringBuilder();
        builder.append("runner=" + runnerLink.get() + "\n");
        builder.append("temp=" + tempLink.get() + "\n");
        builder.append("project=" + projectName.get() + "\n");
        builder.append("args=" + cmdArgs.get() + "\n");
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("res/meta.txt"));
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
        }
    }

    private void inputText(String label, ImString input) {
        float w = ImGui.getContentRegionAvailX();
        ImGui.text(label);
        ImGui.sameLine(w * 1 / 4);
        // ImGui.setNextItemWidth(w * 4 / 4);
        ImGui.inputText("##" + label, input);
    }

    private boolean button(String label) {
        float w = ImGui.calcTextSize(label, false, 0).x;
        float ww = ImGui.getContentRegionAvailX();
        ImGui.setCursorPosX(ww / 2 - w / 2);
        return ImGui.button(label);
    }

    private void title(String label) {
        float w = ImGui.calcTextSize(label, false, 0).x;
        float ww = ImGui.getContentRegionAvailX();
        ImGui.setCursorPosX(ww / 2 - w / 2);
        ImGui.text(label);
        ImGui.separator();
        ImGui.newLine();
    }

    private void execute() throws IOException {
        File lastFolder = getMostRecent(new File(tempLink.get()), projectName.get());
        String location = lastFolder.getAbsolutePath() + "\\" + projectName.get() + ".win";
        if (!new File(runnerLink.get()).exists()) {
            error = "Could not find runner link";
            return;
        }
        if (!new File(location).exists()) {
            error = "Project did not exist";
            return;
        }
        ProcessBuilder builder = new ProcessBuilder(
                runnerLink.get(), "-game", location, cmdArgs.get());
        builder.redirectErrorStream(true).start();
    }

    public File getMostRecent(File folder, String subname) {
        if (folder == null || !folder.exists()) {
            error = "Temp Link folder did not exist";
            return null;
        }
        File mostRecent = null;
        for (File file : folder.listFiles()) {
            if (!file.isDirectory())
                continue;
            if (!file.getName().contains(subname))
                continue;
            long mod = file.lastModified();
            if (mostRecent == null || mostRecent.lastModified() < mod)
                mostRecent = file;
        }
        if (mostRecent == null)
            error = "Could not find a valid folder";
        return mostRecent;
    }
}
