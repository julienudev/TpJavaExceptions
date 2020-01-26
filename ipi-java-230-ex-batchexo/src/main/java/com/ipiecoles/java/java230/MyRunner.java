package com.ipiecoles.java.java230;

import com.ipiecoles.java.java230.exceptions.BatchException;
import com.ipiecoles.java.java230.model.Commercial;
import com.ipiecoles.java.java230.model.Employe;
import com.ipiecoles.java.java230.repository.EmployeRepository;
import com.ipiecoles.java.java230.repository.ManagerRepository;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
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
    private static final String REGEX_IsMTC = "^[MTC]{1}.*";

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private List<Employe> employes = new ArrayList<Employe>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... strings) throws Exception {
        try {
            String fileName = "employes.csv";
            readFile(fileName);
        } catch (FileNotFoundException fileNotFound) {
            System.out.println(fileNotFound.getMessage() + " => fichier introuvable à cet emplacement");
        }
    }

    /**
     * Méthode qui lit le fichier CSV en paramètre afin d'intégrer son contenu en BDD
     *
     * @param fileName Le nom du fichier (à mettre dans src/main/resources)
     * @return une liste contenant les employés à insérer en BDD ou null si le fichier n'a pas pu être le
     */
    public List<Employe> readFile(String fileName) throws Exception {
        Stream<String> stream;
        stream = Files.lines(Paths.get(new ClassPathResource(fileName).getURI()));
        //TODO
        Integer i = 0;
        for (String ligne : stream.collect(Collectors.toList())) {
            i++;
            try {
                processLine(ligne);
            } catch (BatchException batchExc) {
                System.out.println("ligne" + i + " : " + batchExc.getMessage() + " => " + ligne);
            }
        }
        return employes;

    }

    /**
     * Méthode qui regarde le premier caractère de la ligne et appelle la bonne méthode de création d'employé
     *
     * @param ligne la ligne à analyser
     * @throws BatchException si le type d'employé n'a pas été reconnu
     */
    private void processLine(String ligne) throws Exception {


//VERIFICATION TYPE D'EMPLOYE

        ligne.matches(REGEX_IsMTC);
        if (!ligne.matches(REGEX_IsMTC)) {
            throw new BatchException(("type d'employe inconnu" + ligne.charAt(0)));
        }
        String array[] = ligne.split(",");

//EXPRESSION REGULIERE / VERIFICATION FORMAT DU MATRICULE

        if (!array[0].matches(REGEX_MATRICULE)) {
            throw new BatchException("la chaîne " + array[0] + " ne respecte pas l'expression régulière ^[MTC][0-9]{5}$");
        }

// APPEL DE LA BONNE METHODE EN FONCTION DU TYPE D'EMPLOYE
        switch (ligne.charAt(0)) {
            case 'M':
                processManager(ligne);
                break;
            case 'C':
                processCommercial(ligne);
                break;
            case 'T':
                processTechnicien(ligne);
                break;
        }


//TESTE DU SALAIRE

        try {
            if (!(array[4] == null || array[4].length() == 0)) {
                Float.parseFloat(array[4]);
            }


            //System.out.println(Integer.parseInt(array[4]));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(array[4] + " =>pas de salaire");

        } catch (Exception e) {
            throw new BatchException(array[4] + " => n'est pas un nombre valide pour un salaire");
        }


        //TEST FORMAT DATE

        try {
            String date = array[3];
            DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(date);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BatchException("pas de date");
        } catch (Exception e) {
            throw new BatchException(ligne + " ne respecte pas le format de date dd/MM/yyyy ");
        }
    }

    /**
     * Méthode qui crée un Commercial à partir d'une ligne contenant les informations d'un commercial et l'ajoute dans la liste globale des employés
     *
     * @param ligneCommercial la ligne contenant les infos du commercial à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processCommercial(String ligneCommercial) throws BatchException {

        // VERIFICATION DU NB DE CHAMP DE COMMERCIAL

        String arrayCommercial[] = ligneCommercial.split(",");
        if (arrayCommercial.length != NB_CHAMPS_COMMERCIAL) {
            throw new BatchException("La ligne commercial ne contient pas 5 éléments mais " + arrayCommercial.length);
        }

        // TESTE DU CHIFFRE DAFFAIRE

        char chiffreDaffaire = arrayCommercial[5].charAt(0);
        if (!Character.isDigit(chiffreDaffaire)) {
            throw new BatchException("Le chiffre d'affaire du commercial est incorrect : " + chiffreDaffaire);
        }

        // TESTE DE LA PERFORMANCE

        char performance = arrayCommercial[6].charAt(0);
        if (!Character.isDigit(performance)) {
            throw new BatchException("La performance du commercial est incorrect : " + performance);
        }
    }


    /**
     * Méthode qui crée un Manager à partir d'une ligne contenant les informations d'un manager et l'ajoute dans la liste globale des employés
     *
     * @param ligneManager la ligne contenant les infos du manager à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processManager(String ligneManager) throws BatchException {

        // VERIFICATION NB DE CHAMP MANAGER

        String arrayManager[] = ligneManager.split(",");
        if (arrayManager.length != NB_CHAMPS_MANAGER) {
            throw new BatchException("La ligne manager ne contient pas 5 éléments mais " + arrayManager.length);
        }
    }


    /**
     * Méthode qui crée un Technicien à partir d'une ligne contenant les informations d'un technicien et l'ajoute dans la liste globale des employés
     *
     * @param ligneTechnicien la ligne contenant les infos du technicien à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processTechnicien(String ligneTechnicien) throws Exception {

        // VERIFICATION NB DE CHAMP TECHNICIEN

        String arrayTechnicien[] = ligneTechnicien.split(",");
        if (arrayTechnicien.length != NB_CHAMPS_TECHNICIEN) {
            throw new BatchException("La ligne technicien ne contient pas 5 éléments mais " + arrayTechnicien.length);
        }

        // VERIFICATION GRADE

        Integer grade;
        try {
            grade = Integer.parseInt(arrayTechnicien[5]);
        } catch (Exception e) {
            throw new BatchException("Le grade du technicien est incorrect : " + arrayTechnicien[5]);
        }
        if (grade < 1 || grade > 5) {
            throw new BatchException("Le grade doit être compris entre 1 et 5 : " + grade);
        }


        // VERIFICATION DU FORMAT DU MANAGER ATTRIBUE AU TEXCHNICIEN

        if (!arrayTechnicien[6].matches(REGEX_MATRICULE_MANAGER)) {
            throw new BatchException("La chaîne " + arrayTechnicien[6] + " ne respecte pas l'expression régulière ^M[0-9]{5}$ ");
        }

        //VERIFICATION EXISTENCE DU MANAGER EN DB

        if (arrayTechnicien[6].matches(REGEX_MATRICULE_MANAGER)) {
            Employe employe = employeRepository.findByMatricule(arrayTechnicien[6]);
            if (employe == null) {
                throw new BatchException("Le manager de matricule " + arrayTechnicien[6] + " n'a pas été trouvé dans en base de données");
            }
        }
    }
}










