package com.ipiecoles.java.java230;

import com.ipiecoles.java.java230.exceptions.BatchException;
import com.ipiecoles.java.java230.model.Commercial;
import com.ipiecoles.java.java230.model.Employe;
import com.ipiecoles.java.java230.repository.EmployeRepository;
import com.ipiecoles.java.java230.repository.ManagerRepository;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MyRunner implements CommandLineRunner {

    private static final String REGEX_MATRICULE = "^[MTC][0-9]{5}$";
    private static final String REGEX_NOM = ".*";
    private static final String REGEX_PRENOM = ".*";
    private static final int NB_CHAMPS_MANAGER = 5;
    private static final int NB_CHAMPS_TECHNICIEN = 7;
    private static final String REGEX_MATRICULE_MANAGER = "^M[0-9]{5}$";
    private static final int NB_CHAMPS_COMMERCIAL = 7;
    private static final String REGEX_TYPE="^[MTC]{1}.*";

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private List<Employe> employes = new ArrayList<Employe>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... strings) throws Exception {
        String fileName = "employes.csv";
        readFile(fileName);
        //readFile(strings[0]);
    }

    /**
     * Méthode qui lit le fichier CSV en paramètre afin d'intégrer son contenu en BDD
     * @param fileName Le nom du fichier (à mettre dans src/main/resources)
     * @return une liste contenant les employés à insérer en BDD ou null si le fichier n'a pas pu être le
     */
    public List<Employe> readFile(String fileName) throws Exception {
        Stream<String> stream;
        stream = Files.lines(Paths.get(new ClassPathResource(fileName).getURI()));
        //TODO
        Integer i =0;
        for (String ligne : stream.collect(Collectors.toList())){
            i++;
            try {
                processLine(ligne);
                if (ligne.charAt(0)=='M'){
                    processManager(ligne);
                }
                else if (ligne.charAt(0)=='C'){
                    processCommercial(ligne);
                }
                else if (ligne.charAt(0)=='T'){
                    processTechnicien(ligne);
                }

            }
            catch (BatchException e ){
                System.out.println("ligne"+i+" : "+e.getMessage()+" => "+ligne);
            }
        }
        return employes;

    }

    /**
     * Méthode qui regarde le premier caractère de la ligne et appelle la bonne méthode de création d'employé
     * @param ligne la ligne à analyser
     * @throws BatchException si le type d'employé n'a pas été reconnu
     */
    private void processLine(String ligne) throws BatchException {
        //TODO
        int n=0;

//EXPRESSION REGULIERE / VERIFICATION TYPE D'EMPLOYE
        ligne.matches(REGEX_TYPE);
        if(!ligne.matches(REGEX_TYPE)){
            throw new BatchException(("type d'employe inconnu"+ ligne.charAt(0)));
        }

//EXPRESSION REGULIERE / VERIFICATION MATRICULE

        String array[] = ligne.split(",");
        //if(arr[0].length()!=6){
        if (!array[0].matches(REGEX_MATRICULE)){
            throw new BatchException("la chaîne "+ array[0] +" ne respecte pas l'expression régulière ^[MTC][0-9]{5}$");
        }

//TESTE DU SALAIRE

        if (array.length>=4) {
            //System.out.println(arr.length);
            char salaire = array[4].charAt(0);
            if (!Character.isDigit(salaire)) {
               throw new BatchException("sdf n'est pas un nombre valide pour un salaire");
            }
        }


//TEST FORMAT DATE
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        //sdf.setLenient(false);

        /*try
        {
            LocalDate localDate = LocalDate.parse( arr[3] );
            //System.out.println( "localDate.toString():  " + localDate ) ;
        } catch ( DateTimeParseException e )
        {
            throw new BatchException("04/99/2013 ne respecte pas le format de date dd/MM/yyyy");
            // … handle exception
            //System.out.println( e.getLocalizedMessage( ) );
        }*/

    }

    /**
     * Méthode qui crée un Commercial à partir d'une ligne contenant les informations d'un commercial et l'ajoute dans la liste globale des employés
     * @param ligneCommercial la ligne contenant les infos du commercial à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processCommercial(String ligneCommercial) throws BatchException {
        //TODO
        int numberOfWord = ligneCommercial.split(",").length;
        //if(arr[0].length()!=6){
        if (numberOfWord != 7){
            throw new BatchException("La ligne manager ne contient pas 5 éléments mais "+numberOfWord);
        }
        if (numberOfWord != 7){
            throw new BatchException("La ligne manager ne contient pas 5 éléments mais "+numberOfWord);
        }

        //TESTE DU CHIFFRE DAFFAIRE
        String arr[] = ligneCommercial.split((","));




        if (numberOfWord >= 6) {
            //System.out.println(arr.length);
           char chiffreDaffaire = arr[5].charAt(0);
            if (!Character.isDigit(chiffreDaffaire)) {
                throw new BatchException("Le chiffre d'affaire du commercial est incorrect : " + chiffreDaffaire);
            }

//TESTE DE LA PERFORMANCE
            if (numberOfWord >= 7) {
                char performance = arr[6].charAt(0);
                if (!Character.isDigit(performance)) {
                    throw new BatchException("La performance du commercial est incorrect : " + performance);
                }
            }
        }



    }

    /**
     * Méthode qui crée un Manager à partir d'une ligne contenant les informations d'un manager et l'ajoute dans la liste globale des employés
     * @param ligneManager la ligne contenant les infos du manager à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processManager(String ligneManager) throws BatchException {
//TODO
//NOMBRE ELEMENT DE LA LIGNE

        int numberOfWord = ligneManager.split(",").length;
        //if(arr[0].length()!=6){
        if (numberOfWord != 5){
            throw new BatchException("La ligne manager ne contient pas 5 éléments mais "+numberOfWord);
        }
        if (numberOfWord != 5){
            throw new BatchException("La ligne manager ne contient pas 5 éléments mais "+numberOfWord);
        }


    }

    /**
     * Méthode qui crée un Technicien à partir d'une ligne contenant les informations d'un technicien et l'ajoute dans la liste globale des employés
     * @param ligneTechnicien la ligne contenant les infos du technicien à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processTechnicien(String ligneTechnicien) throws BatchException {
        //TODO
        //int numberOfWord = ligneTechnicien.split(",").length;
        String arr[] = ligneTechnicien.split(",");
        //if(arr[0].length()!=6){


        if (ligneTechnicien.split(",").length == 6) {
            throw new BatchException("La ligne technicien ne contient pas 7 éléments mais " + 6);
        }


        if (ligneTechnicien.split(",").length == 9) {
            throw new BatchException("La ligne technicien ne contient pas 7 éléments mais " + 9);
        }


        //TESTE GRADE

        if (ligneTechnicien.split(",").length >= 7) {
            int grade = arr[5].charAt(0);

            if (!Character.isDigit(grade)) {
                throw new BatchException(("Le grade du technicien est incorrect"));
            }
            if (grade <= 1 || grade >= 5) {
                throw new BatchException(("Le grade doit être compris entre 1 et 5 : "));
            }


            String matriManag = arr[6];
            if (!matriManag.matches(REGEX_MATRICULE_MANAGER)) {
                throw new BatchException("la chaîne xxx ne respecte pas l'expression régulière");

            }
            try {
                if (employeRepository.findByMatricule(matriManag) != null){};
                /*if (e.getMatricule() == arr[6]) {
                    throw new BatchException("Le manager de matricule n'a pas été trouvé dans le fichier ou en base de données");
                }*/

            }
            catch (NullPointerException n)
                        {
                            throw new NullPointerException("Le manager de matricule n'a pas été trouvé dans le fichier ou en base de données");
                        }
        }
    }








}
