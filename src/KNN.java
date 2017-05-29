import java.io.*;
import java.util.*;

/**
 * Created by shwetabarapatre on 29/03/17.
 */
public class KNN {
    ArrayList<Iris> trainingSet;
    ArrayList<Iris> testSet;

    private ArrayList<Iris> loadFromFile(BufferedReader reader){
        ArrayList<Iris> listOfIris = new ArrayList<Iris>();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("  ");
                    if(parts.length==5) {
                        Iris iris = new Iris(Double.parseDouble(parts[0]),Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),Double.parseDouble(parts[3]),parts[4]);
                        listOfIris.add(iris);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listOfIris;
    }

    private void loadData(String trainingData, String testData) throws FileNotFoundException {
        trainingSet = loadFromFile(new BufferedReader(new FileReader(new File(trainingData))));
        testSet = loadFromFile(new BufferedReader(new FileReader(new File(testData))));
    }

    private Double calculateDistance(Iris i1, Iris i2){
        Double petalLength1 = i1.getPetalLength();
        Double petalWidth1 = i1.getPetalWidth();
        Double sepalLength1 = i1.getSepalLength();
        Double sepalWidth1 = i1.getSepalWidth();

        Double petalLength2 = i2.getPetalLength();
        Double petalWidth2 = i2.getPetalWidth();
        Double sepalLength2 = i2.getSepalLength();
        Double sepalWidth2 = i2.getSepalWidth();

        Double petalLengthRange = calculateRange(trainingSet, "petalLength");
        Double petalWidthRange = calculateRange(trainingSet, "petalWidth");
        Double sepalLengthRange = calculateRange(trainingSet, "sepalLength");
        Double sepalWidthRange = calculateRange(trainingSet, "sepalWidth");

        Double PetalLength = Math.pow((petalLength1-petalLength2), 2)/Math.pow(petalLengthRange, 2);
        Double PetalWidth = Math.pow((petalWidth1-petalWidth2),2)/Math.pow(petalWidthRange, 2);
        Double SepalLength = Math.pow((sepalLength1-sepalLength2),2)/Math.pow(sepalLengthRange, 2);
        Double SepalWidth = Math.pow((sepalWidth1-sepalWidth2),2)/Math.pow(sepalWidthRange, 2);

        return Math.sqrt(PetalLength+PetalWidth+SepalLength+SepalWidth);
    }

    private double calculateRange(List<Iris> trainingSet, String variable){
        List<Double> values = new ArrayList<Double>();
            if(variable.equals("sepalLength")){
                for(Iris i : trainingSet){
                    values.add(i.getSepalLength());

                }
            }
            else if (variable.equals("sepalWidth")){
                for(Iris i : trainingSet){
                    values.add(i.getSepalWidth());

                }
            }
            else if (variable.equals("petalLength")){
                for(Iris i : trainingSet){
                    values.add(i.getPetalLength());

                }
            }
            else{
                for(Iris i : trainingSet){
                    values.add(i.getPetalWidth());

                }
            }

            Collections.sort(values);
            double range = Collections.max(values)-Collections.min(values);
            return range;
    }

    private List<Iris> findNeighbours(List<Iris> trainingSet, Iris testIris, int k){
        Map<Double, Iris> distanceFromTrainingSetIris = new HashMap<Double, Iris>();
        List<Double> distances = new ArrayList<Double>();
        for(int i =0; i<trainingSet.size();i++){
            Double distanceFromTestIris = calculateDistance(trainingSet.get(i), testIris);
            distanceFromTrainingSetIris.put(distanceFromTestIris, trainingSet.get(i));
            distances.add(distanceFromTestIris);
        }
        List<Iris> nearestNeighbours = new ArrayList<Iris>();
        Collections.sort(distances);
        for(int i = 0; i<k; i++){
            nearestNeighbours.add(distanceFromTrainingSetIris.get(distances.get(i)));
        }
        return nearestNeighbours;

    }

    private String classVote(List<Iris> nearestNeighbours){
        List<String> typesInNeighbours = new ArrayList<String>();
        for(int i = 0; i<nearestNeighbours.size();i++){
            typesInNeighbours.add(nearestNeighbours.get(i).getType());
        }
        int count = 0;
        int occurrencesOfSetosa = Collections.frequency(typesInNeighbours, "Iris-setosa");
        int occurrencesOfVersicolor = Collections.frequency(typesInNeighbours, "Iris-versicolor");
        int occurrencesOfVirginica = Collections.frequency(typesInNeighbours, "Iris-virginica");
        String most = "";
        if ( occurrencesOfSetosa > occurrencesOfVersicolor && occurrencesOfSetosa > occurrencesOfVirginica ) {
            most = "Iris-setosa";
        }
        else if ( occurrencesOfVersicolor > occurrencesOfSetosa && occurrencesOfVersicolor > occurrencesOfVirginica ) {
            most = "Iris-versicolor";
        }
        else if ( occurrencesOfVirginica > occurrencesOfSetosa && occurrencesOfVirginica > occurrencesOfVersicolor ) {
            most = "Iris-virginica";
        }
        else {
            //amounts are not different
            most = "Iris-setosa";
        }
        return most;
    }


    private void startAlgorithm() {
        double correct = 0;
        int count = 0;
        for (Iris i : testSet) {
            count++;
            List<Iris> neighbours = findNeighbours(trainingSet, i, 3);
            String prediction = classVote(neighbours);
            if(i.getType().equals(prediction)){
                correct++;
            }
            System.out.println("Iris "+ count+": "+ i.getSepalLength()+" "+i.getSepalLength()+" "+i.getPetalLength()+" "+i.getPetalWidth()+" "+"Actual: "+i.getType()+" Prediction: "+prediction);
        }
        double accuracy = (correct/testSet.size())*100;
        System.out.println("Accuracy: "+ Math.round(accuracy)+"%");
    }

    public static void main(String[] args) throws FileNotFoundException {

        KNN main = new KNN();
        main.loadData(args[0], args[1]);
        main.startAlgorithm();

    }
}
