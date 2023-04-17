package bgu.spl.mics.application;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

// Java program to read JSON from a file

import java.util.Vector;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {

    private static final Cluster cluster = Cluster.getInstance();
    private static final MessageBusImpl MB = MessageBusImpl.getInstance();
    private static final JsonParser jsonParser = new JsonParser();
    public static CountDownLatch countDownForTimer;

    public static CountDownLatch countDownForStudent;

    public static void main(String[] args) throws InterruptedException {
        InputStream inputStream = null;
        try { inputStream = new FileInputStream(args[0]);}
        catch (FileNotFoundException ignored) {}
        Reader reader = new InputStreamReader(inputStream);
        JsonElement rootElement = jsonParser.parse(reader);
        JsonObject rootObject = rootElement.getAsJsonObject();
        Vector<Student> studentVector = new Vector<Student>();
        Vector<GPU> gpuVector = new Vector<GPU>();
        Vector<CPU> cpuVector = new Vector<>();
        Vector<ConferenceService> conferenceServiceVector= new Vector<>();
        JsonArray students= rootObject.getAsJsonArray("Students");

        for(JsonElement  student : students) {
            String name = student.getAsJsonObject().get("name").getAsString();
            String department = student.getAsJsonObject().get("department").getAsString();
            String status = student.getAsJsonObject().get("status").getAsString();
            //Vector<Model> modelsVector=new Vector<Model>();
            Student newStudent;
            if (status == "MSc") newStudent = new Student(name, department, Student.Degree.MSc);
            else newStudent = new Student(name, department, Student.Degree.PhD);

            JsonArray models = student.getAsJsonObject().getAsJsonArray("models");
            for (JsonElement model : models) {
                String MName = model.getAsJsonObject().get("name").getAsString();
                int MSize = model.getAsJsonObject().get("size").getAsInt();
                String MType = model.getAsJsonObject().get("type").getAsString();
                Data MData;
                if (MType.equals("Images")) MData = new Data(Data.Type.Images, MSize);
                else if (MType.equals("Text")) MData = new Data(Data.Type.Text, MSize);
                else MData = new Data(Data.Type.Tabular, MSize);
                newStudent.addModle(new Model(MName, MData, newStudent));
            }
            studentVector.add(newStudent);

        }
            //parse GPUS
            JsonArray GPUS= rootObject.getAsJsonArray("GPUS");

            for(JsonElement gpu:GPUS){
                String type=gpu.getAsString();
                GPU currGPU;
                if(type.equals("RTX3090")) {
                    currGPU= new GPU(GPU.Type.RTX3090,cluster);
                }
                else if(type.equals("RTX2080")) {
                    currGPU= new GPU(GPU.Type.RTX2080,cluster);
                }
                else {
                    currGPU= new GPU(GPU.Type.GTX1080,cluster);
                }
                gpuVector.add(currGPU);
            }

            //parse CPUS
            JsonArray CPUS= rootObject.getAsJsonArray("CPUS");
            for(JsonElement cpu:CPUS){
                CPU currCPU=new CPU(cpu.getAsInt());
                cpuVector.add(currCPU);
            }

            //parse Conferences
            JsonArray Conferences= rootObject.getAsJsonArray("Conferences");
            for(JsonElement conference:Conferences){

                String CName=conference.getAsJsonObject().get("name").getAsString();
                int CDate=conference.getAsJsonObject().get("date").getAsInt();
                ConfrenceInformation confrenceInformation=new ConfrenceInformation(CName,CDate);
                ConferenceService conferenceService=new ConferenceService(CName, confrenceInformation);
                conferenceServiceVector.add(conferenceService);
            }

            //parse tickTime & tickDur
            int tickTime = rootObject.get("TickTime").getAsInt();
            int duration = rootObject.get("Duration").getAsInt();
            TimeService timeService=new TimeService(tickTime, duration);
            Thread threadTicks = new Thread(timeService);
            cluster.addGPUs(gpuVector);
            cluster.addCPUs(cpuVector);


        countDownForTimer = new CountDownLatch(cpuVector.size()+ studentVector.size()+ gpuVector.size()+ conferenceServiceVector.size());
        countDownForStudent = new CountDownLatch(cpuVector.size()+gpuVector.size()+conferenceServiceVector.size());
        int i=1;
        Vector<GPUService> gpuServices = new Vector<>();
        Vector<CPUService> cpuServices = new Vector<>();
        Vector<Thread> threads= new Vector<>();
        for(GPU gpu : gpuVector){
            GPUService gpuService = new GPUService("GPU"+i, gpu);
            gpuServices.add(gpuService);
            Thread thread = new Thread(gpuService);
            threads.add(thread);
            thread.start();
            i++;
        }
        i=0;
        for(CPU cpu : cpuVector){
            CPUService cpuService = new CPUService("CPU"+i,cpu);
            cpuServices.add(cpuService);
            Thread thread = new Thread(cpuService);
            threads.add(thread);
            thread.start();
            i++;
        }
        for(ConferenceService conferenceService : conferenceServiceVector){
            Thread thread = new Thread(conferenceService);
            threads.add(thread);
            thread.start();
        }
        try{
            countDownForStudent.await();
        }catch (InterruptedException e){};


        for(Student student : studentVector){
            StudentService studentService = new StudentService(student.getName(),student);
            Thread thread = new Thread(studentService);
            threads.add(thread);
            thread.start();
        }


        try{
            countDownForTimer.await();
        }catch (InterruptedException e){};
        threads.add(threadTicks);
        threadTicks.start();
        threadTicks.join();
        for (Thread thread : threads) {
            thread.interrupt();
        }

        for( Thread thread : threads){
            thread.join();
        }
//
//        for(Student student : studentVector){
//            System.out.println(student.getName());
//            System.out.println(student.getPapersRead());
//            for (Model model : student.getModels()) {
//                System.out.println(model.getName()+" status: "+model.getStatus());
//                System.out.println("processed: "+model.getData().getProcessed());
//            }
//
//        }
//        for(CPUService cpu : cpuServices) {
//            System.out.println(cpu.getName()+"with "+cpu.getCPU().getCores()+" cores ticks used: "+cpu.getTotalTime()+" batches processed: "+cpu.getTotalBatchesProcessed());
//        }
//        for (GPUService gpu : gpuServices) {
//            System.out.println(gpu.getName()+"with processing time "+gpu.getGPU().getProcessingTime()+" ticks used: "+gpu.getTimeUnitsUsed()+" batches processed: "+gpu.getTotalBatchesProcessed());
//        }
//
//
//
//
//
//        System.out.println("*********************");


        //Create Output json.
        try {
            JSONObject sampleObject = new JSONObject();
            JSONArray studentsJson = new JSONArray();
            JSONArray conffJson = new JSONArray();

            for (Student student : studentVector) {
                JSONObject studentObject = new JSONObject();
                studentObject.put("name",student.getName());
                studentObject.put("department",student.getdDepartment());
                studentObject.put("publications",student.getPublications());
                studentObject.put("paperRead", student.getReadConff().size());
                JSONArray trainedModels = new JSONArray();
                for(Model model : student.getModels()){
                    if(model.getStatus()==Model.Degree.Tested | model.getStatus()==Model.Degree.Trained | model.getStatus()==Model.Degree.Posted){
                        JSONObject modelObject = new JSONObject();
                        modelObject.put("name",model.getName());
                        JSONObject dataObject = new JSONObject();
                        dataObject.put("type",model.getData().getType().toString());
                        dataObject.put("size",model.getData().getSize());
                        modelObject.put("data",dataObject);
                        if (model.getStatus() != Model.Degree.Posted) modelObject.put("status",model.getStatus().toString());
                        else modelObject.put("status",Model.Degree.Tested.toString());
                        modelObject.put("results",model.getResult().toString());
                        trainedModels.add(modelObject);
                    }
                }
                studentObject.put("trainedModels",trainedModels);
                studentsJson.add(studentObject);


            }


            for(ConferenceService conferenceService : conferenceServiceVector){
                JSONObject confObject = new JSONObject();
                confObject.put("name", conferenceService.getName());
                confObject.put("date", conferenceService.getConfrenceInf().getDate());
                JSONArray publications = new JSONArray();
                for(Model model : conferenceService.getModels()){
                    if(model.getStatus()==Model.Degree.Posted){
                        JSONObject modelObject = new JSONObject();
                        modelObject.put("name",model.getName());
                        JSONObject dataObject = new JSONObject();
                        dataObject.put("type",model.getData().getType().toString());
                        dataObject.put("size",model.getData().getSize());
                        modelObject.put("data",dataObject);

                        modelObject.put("status",Model.Degree.Tested.toString());
                        modelObject.put("results",model.getResult().toString());
                        publications.add(modelObject);
                    }

                }
                confObject.put("publications",publications);
                conffJson.add(confObject);

            }


            int cpuTimeUsed=0;
            for(CPUService cpuService : cpuServices){
                cpuTimeUsed+=cpuService.getCPU().getTotalTick();
            }
            int gpuTimeUsed=0;
            int batchesProcessed=0;
            for(GPUService gpuService : gpuServices){
                gpuTimeUsed+=gpuService.getTimeUnitsUsed();
                batchesProcessed+=gpuService.getTotalBatchesProcessed();
            }

            sampleObject.put("students:", studentsJson);
            sampleObject.put("conferences:", conffJson);
            sampleObject.put("cpuTimeUsed:", cpuTimeUsed);
            sampleObject.put("gpuTimeUsed:", gpuTimeUsed);
            sampleObject.put("batchesProcessed:", batchesProcessed);



            Gson gson= new GsonBuilder().setPrettyPrinting().create();
            String prettyJsonString = gson.toJson(sampleObject);
            Files.write(Paths.get("./Output.json"), prettyJsonString.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
