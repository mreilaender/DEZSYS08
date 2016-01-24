package at.reilaender;

import com.nativelibs4java.opencl.*;
import com.nativelibs4java.util.IOUtils;
import org.bridj.Pointer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import static org.bridj.Pointer.allocateFloats;
import static org.bridj.Pointer.allocateInts;

/**
 * @author manue
 * @version 22.01.2016
 */
public class Main {
    /* Ends in a bluescreen TODO debug */
    public static void test1() {
        CLContext context = JavaCL.createBestContext();
        CLQueue queue = context.createDefaultQueue();
        ByteOrder byteOrder = context.getByteOrder();

        int n = 1024;
        Pointer<Float>
                aPtr = allocateFloats(n).order(byteOrder),
                bPtr = allocateFloats(n).order(byteOrder);

        for (int i = 0; i < n; i++) {
            aPtr.set(i, (float)Math.cos(i));
            bPtr.set(i, (float)Math.sin(i));
        }

        // Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
        CLBuffer<Float>
                a = context.createBuffer(CLMem.Usage.Input, aPtr),
                b = context.createBuffer(CLMem.Usage.Input, bPtr);

        // Create an OpenCL output buffer :
        /* TODO made by me */
        final Pointer<Float> nPtr = Pointer.allocateFloats(n).order(byteOrder);
        CLBuffer<Float> out = context.createBuffer(CLMem.Usage.Output, nPtr);

        // Read the program sources and compile them :
        /* Get resource file */
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        //InputStream is = classloader.getResourceAsStream("TutorialKernels.cl");
        String src = null;
        try {
            src = IOUtils.readText(classloader.getResource("opencl.cl"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CLProgram program = context.createProgram(src);

        // Get and call the kernel :
        CLKernel addFloatsKernel = program.createKernel("add_floats");
        addFloatsKernel.setArgs(a, b, out, n);
        CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, new int[] { n });

        Pointer<Float> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

        // Print the first 10 output values :
        for (int i = 0; i < 10 && i < n; i++)
            System.out.println("out[" + i + "] = " + outPtr.get(i));
    }

    public static void test2() {
        CLContext clContext = JavaCL.createBestContext();
        CLDevice[] devices = clContext.getDevices();

        System.out.println("The following devices have been found");
        System.out.println("Devices: " + devices.length);
        for(int i=0;i < devices.length;++i) {
            System.out.println(String.format("[%d] Found device: %s", i, devices[i]));
        }
    }
    public static void test3() {
        CLContext context = JavaCL.createBestContext();
        CLQueue queue = context.createDefaultQueue();
        ByteOrder byteOrder = context.getByteOrder();

        int n = 1024;
        Pointer<Integer>
                aPtr = allocateInts(n).order(byteOrder),
                bPtr = allocateInts(n).order(byteOrder);

        aPtr.set(0, 1);
        bPtr.set(0, 100);

        // Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
        CLBuffer<Integer>
                a = context.createBuffer(CLMem.Usage.Input, aPtr),
                b = context.createBuffer(CLMem.Usage.Input, bPtr);

        // Create an OpenCL output buffer :
        /* TODO made by me */
        final Pointer<Float> nPtr = Pointer.allocateFloats(n).order(byteOrder);
        CLBuffer<Float> out = context.createBuffer(CLMem.Usage.Output, nPtr);

        // Read the program sources and compile them :
        /* Get resource file */
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        //InputStream is = classloader.getResourceAsStream("TutorialKernels.cl");
        String src = null;
        try {
            src = IOUtils.readText(classloader.getResource("opencl.cl"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CLProgram program = context.createProgram(src);

        // Get and call the kernel :
        CLKernel addFloatsKernel = program.createKernel("calculate_pi");
        addFloatsKernel.setArgs(a, b, out, n);
        CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, new int[] { n });

        Pointer<Float> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

        // Print the first 10 output values :
        for (int i = 0; i < 10 && i < n; i++)
            System.out.println("out[" + i + "] = " + outPtr.get(i));
    }
    public static void main(String[] args) {
        test3();
    }
}