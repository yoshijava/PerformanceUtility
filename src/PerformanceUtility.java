import java.io.*;
import java.util.*;
import android.util.*;
import java.util.regex.*;
// import com.android.uiautomator.platform.SurfaceFlingerHelper;

public class PerformanceUtility {
    public static int getCurrentFreq(int core) {
        String path = "/sys/devices/system/cpu/cpu" + core + "/cpufreq/scaling_cur_freq";
        String freq = null;
        try {
            freq = UtilityClass.justGetFirstLine(path);
            int freqValue = Integer.parseInt(freq);
            return freqValue;
        }
        catch(IOException e) {
            UtilityClass.logd(path + " not found");
            return -1;
        }
    }

    public static int getCurrentMaxFreq(int core) {
        String path = "/sys/devices/system/cpu/cpu" + core + "/cpufreq/scaling_max_freq";
        String freq = null;
        try {
            freq = UtilityClass.justGetFirstLine(path);
            int freqValue = Integer.parseInt(freq);
            return freqValue;
        }
        catch(IOException e) {
            UtilityClass.logd(path + " not found");
            return -1;
        }
    }


    public static int[] getAvailableFrequencies(int core){
        String path = "/sys/devices/system/cpu/cpu" + core + "/cpufreq/scaling_available_frequencies";
        String freqs = null;
        try {
            freqs = UtilityClass.justGetFirstLine(path);
            String[] freq = freqs.split(" ");
            int[] freqValue = new int[freq.length];
            for(int i=0; i<freq.length; i++) {
                freqValue[i] = Integer.parseInt(freq[i]);
            }
            return freqValue;
        }
        catch(IOException e) {
            UtilityClass.logd(path + " not found");
            return null;
        }
    }

    public static int freqCeil(int core, int curFreq) {
        int[] frequencies = getAvailableFrequencies(core);
        for(int i=frequencies.length-1; i>=0; i--) {
            // find the frequencies > current one
            if(frequencies[i] > curFreq) {
                return frequencies[i];
            }
        }
        // no one is greater than current freq. Just return the max freq.
        return frequencies[0];
    }

    public static int freqFloor(int core, int curFreq) {
        int[] frequencies = getAvailableFrequencies(core);
        for(int i=0; i<frequencies.length; i++) {
            // find the frequencies < current one
            if (frequencies[i] < curFreq) {
                return frequencies[i];
            }
        }
        // no one is greater than current freq. Just return the min freq.
        return frequencies[frequencies.length-1];
    }

    public static void setMaxCpuFreq(int core, int freq) {
        String path = "/sys/devices/system/cpu/cpu" + core + "/cpufreq/scaling_max_freq";
        UtilityClass.echo(freq, path);
    }

    public static void setMinCpuFreq(int core, int freq) {
        String path = "/sys/devices/system/cpu/cpu" + core + "/cpufreq/scaling_min_freq";
        UtilityClass.echo(freq, path);
    }

    public static int getMinCpuFreq(int core){
        String path = "/sys/devices/system/cpu/cpu" + core + "/cpufreq/scaling_min_freq";
        int freq = -1;
        try {
            freq = Integer.parseInt(UtilityClass.justGetFirstLine(path));
        }
        catch(IOException e) {
            UtilityClass.logd(path + " not found");
            return -1;
        }
        return freq;
    }

    public static int getMaxCpuFreq(int core){
        String path = "/sys/devices/system/cpu/cpu" + core + "/cpufreq/scaling_max_freq";
        int freq = -1;
        try {
            freq = Integer.parseInt(UtilityClass.justGetFirstLine(path));
        }
        catch(IOException e) {
            UtilityClass.logd(path + " not found");
            return -1;
        }
        return freq;
    }

    public static int getNumOfOnlineCpu() {
        int online = 0;
        for(int i=0; i<getMaxNumOfCores(); i++) {
            if(isCpuOnline(i) == true) {
                online++;
            }
        }
        return online;
    }

    public static void setNumOfOnlineCpu(int toOnline) {
        String path = "/sys/devices/system/cpu/cpu" + toOnline + "/online";
        for(int i=0; i<toOnline; i++) {
            UtilityClass.echo(1, path);
        }
    }

    public static boolean isCpuOnline(int core){
        String path = "/sys/devices/system/cpu/cpu" + core + "/online";
        int on = -1;
        try {
            on = Integer.parseInt(UtilityClass.justGetFirstLine(path));
        }
        catch(IOException e) {
            UtilityClass.logd(path + " not found");
            return false;
        }
        if(on == 1) {
            return true;
        }
        else {
            return false;
        }
    }

    public static String getGovernorName(int core) {
        String path = "/sys/devices/system/cpu/cpu" + core + "/cpufreq/scaling_governor";
        String governor = null;
        try {
            governor = UtilityClass.justGetFirstLine(path);
        }
        catch(IOException e) {
            UtilityClass.logd(path + " not found");
            return null;
        }
        return governor;
    }

    public static int getMaxNumOfCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname)
            {
            //Check if filename is "cpu", followed by a single digit number
                if(Pattern.matches("cpu[0-9]+", pathname.getName()))
                {
                    return true;
                }
                return false;
            }
        }
        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        }
        catch(Exception e) {
            return -1;
        }
        // String path = "/sys/devices/system/cpu/kernel_max";
        // int nCores = Integer.parseInt(UtilityClass.justGetFirstLineIgnoreException(path)) + 1;
        // return nCores;
    }

     public static double getFPS() {
        // warning: android only.
        try {
            java.lang.Process process = java.lang.Runtime.getRuntime().exec("logcat -d | grep SurfaceFlinger");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }
            Pattern pattern = Pattern.compile("fps:([0-9]*\\.?[0-9]+)");
            Matcher m = pattern.matcher(log.toString());
            String fps = "";
            while (m.find()) {
                fps = m.group(1);
            }
            if(fps.equals("")) {
                return -1;
            }
            else {
                double fpsDouble = Double.parseDouble( fps );
                UtilityClass.logd("FPS = " + fpsDouble);
                return fpsDouble;
            }
        }
        catch (IOException e) {
            UtilityClass.logd("Get FPS exception", e);
        }
        return -1;

        // it doesn't work
        // return SurfaceFlingerHelper.getFrameRate();
    }
    // Read hotplug status
    public static boolean isHotplugEnabled(int core){
        String path = "/proc/hps/enabled";
        int on = -1;
        try {
            on = Integer.parseInt(UtilityClass.justGetFirstLine(path));
        }
        catch(IOException e) {
            UtilityClass.logd(path + " not found");
            return false;
        }
        if(on == 1) {
            return true;
        }
        else {
            return false;
        }
    }

    public static void resetAll() {
        setHotplug(1);
        // Currently we don't have file access permission of governor
        // setGovernor(0, "ondemand");
        setMaxCpuFreq(0, getMaxCpuFreq(0));
        setMinCpuFreq(0, getMinCpuFreq(0));
    }

    // 1:enable hotplug, 0:disalbe hotplug
    public static void setHotplug(int val) {
        String path = "/proc/hps/enabled";
        UtilityClass.echo(val, path);
    }

    // set governor
    public static void setGovernor(int core, String new_governor) {
        String path = "/sys/devices/system/cpu/cpu" + core + "/cpufreq/scaling_governor";
        UtilityClass.echo(new_governor,path);
    }
}
