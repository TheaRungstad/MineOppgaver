/** @author Thea Therese Rungstad */

import java.util.Scanner;


/** Dette programmet lar en bruker opprette arbeidsoppgaver etter prioritet, og disse legges til 
 * en liste brukeren kan lese ved behov. Brukeren kan også fjerne arbeidsoppgaver etter hvert
 * som de er utført. Ved programslutt slettes alle oppgaver, men ved en eventuell utvidelse av
 * programmet kan arbeidsoppgavene f.eks. skrives til en fil. */
class MineOppgaver {
    public static void main(String[] args) {

        UserInteraction userInteraction = new UserInteraction();
        userInteraction.startMenu();
    
    }
}


/** Klassen UserInteraction er det øvre "laget" i dette programmet. Kan på sikt utvides/revideres
 * med bruk av Frames
 */
class UserInteraction {
   
    TaskController controller = null; //peker på TaskController objektet som lages i setUp()
    int choice = 0;
    boolean run = true;
    Scanner myCmnd = null;


    /** konstruktøren lager et objekt av klassen TaskController 
     * */
    public UserInteraction() {
        controller = new TaskController();
    }


    /** metoden startMenu() gir brukeren en enkel meny til å manøvrere i programmet. 
    * Når metoden avsluttes går tråden tilbake til main som så avslutter programmet 
    */
    public void startMenu() {
        while (run){ // Kjører startmeny helt til bruker velger 4: Avslutt
            System.out.println("\nVelkommen til Mine oppgaver!\n"
            + "\nMENY\n"
            + "1. Legg til ny oppgave\n"
            + "2. Fjern oppgave\n"
            + "3. Se Mine oppgaver\n"
            + "4. Avslutt\n");
      
            myCmnd = new Scanner(System.in);
          
            System.out.println("Valg:");
        

            if (myCmnd.hasNextInt()) {
                choice = myCmnd.nextInt();  // Leser brukerinput dersom integer
                if (choice < 1 || choice > 4) { // Dersom integer - undersøker om gyldig kommando 1-4
                    System.out.println("\nUgyldig kommando.\n");
                } else {
                    switch (choice) { // Går videre til riktig programdel utifra brukerens valg
                        case 1:
                            promptTask(myCmnd);
                            break;
                        case 2:  
                            if (promptRemoveTask(myCmnd)) {
                                System.out.println("Oppgaven ble fjernet.");
                            } else {
                                System.out.println("Oppgaven eksisterer ikke og ble derfor ikke fjernet.");
                            }break;
                        case 3: 
                            controller.printTaskList();
                            break;
                        case 4:
                            System.out.println("\nAvslutter programmet.\n");
                            run = false; // Avslutter programmet
                            break;
                    } 
                }
            } else { // Input er ikke integer
                System.out.println("\nUgyldig kommando.\n");
            }            
        }
        myCmnd.close();
    }


    /** Metoden promptTask() etterspør brukerens oppgave og prioritet, 
     * samt kontrollerer at formatet er korrekt. Argumentet peker på
     * Scannerobjektet som ble opprettet i startMenu().  
     */
    public void promptTask (Scanner taskReader){
        String taskString = null;
        int priority = 0;

        System.out.println("Vennligst skriv inn oppgave:");
        
        taskString = taskReader.nextLine(); // leser forrige linjeskift
        taskString = taskReader.nextLine(); // skriver over taskString med brukerens oppgave
        
        System.out.println("\nHvilken prioritet har oppgaven?\n(Fra 1 til 3 der 1 = høyeste prioritet og 3 = laveste prioritet)");

        if (taskReader.hasNextInt()) {
            priority = taskReader.nextInt();  // Leser brukerinput dersom integer
            if (priority < 1 || priority > 3) { // Dersom integer - undersøker om gyldig prioritet 1-3
                System.out.println("\nUgyldig prioritet.\n");
            } else { // Oppgave og prioritet er korrekt oppgitt
                controller.createTask(taskString, priority); // gå til controller som vil behandle oppgaven videre
            }
        } else { // Input er ikke integer
            System.out.println("\nUgyldig kommando i promtTask().\n");
        }
    }
    

    /** Metoden promptRemoveTask() etterspør oppgavens ID (som bruker kan finne under Valg 3 i menyen),
     * samt kontrollerer at formatet er korrekt. Deretter kalles metoden removeTask() i TaskControllerobjektet.
     * Argumentet i promtRemoveTask() peker på Scannerobjektet som ble opprettet i startMenu().
     */
    public boolean promptRemoveTask(Scanner taskReader) {
       
        int id = 0;
        boolean answer = false;

        System.out.println("\nHvilken ID har oppgaven?\n(Tips - se oversikt under valg 3)");
        
        taskReader.nextLine(); // leser forrige linjeskift

        if (taskReader.hasNextInt()) {
            
            id = taskReader.nextInt();  // Leser brukerinput dersom integer
                answer = controller.removeTask(id); // gå til controller som vil behandle oppgaven videre, returner svaret
        } else { // Input er ikke integer
            System.out.println("\nUgyldig kommando i promtRemoveTask().\n");
        }
        return answer;
    }
}



class TaskController {
    int presentTaskID = 1;
    Task presentTask = null;
    TaskList list = null;

    /** Konstruktøren lager et objekt av TaskList */
    public TaskController() {
        list = new TaskList();
    }

   /** Metoden createTask() lager et nytt objekt Task. Undersøker om listen er tom eller ei - NØDVENDIG???, 
    * og kaller deretter insertTask() som setter Task inn i TaskList på rett sted
    */
    public void createTask(String task, int priority) {
        /** @TODO vudere: skal denne returnere en boolean for å indikere suksess?*/


        presentTask = new Task(); // oppretter et objekt av klassen Task
        presentTask.taskID = presentTaskID++; // legger til en unik TaskID som økes for hver gang createTask() kalles
        presentTask.taskContent = task;
        presentTask.priority = priority; 
       
        insertTask();
    }


    /** Metoden insertTask() skal sette den enkelte Task inn i TaskList etter prioritet 1-3.
     * Leter gjennom listen og finner første Task med lavere prioritet enn nåværende Task. 
     * Den nåværende Task settes inn FORAN funnet Task. Dermed vil etter hvert alle Taskobjekter 
     * med samme prioritet komme etter hverandre i listen, f.eks.: 1-1-2-2-2-3-3.
     *  */
    public void insertTask() {

        Task examinatedTask = null; // Taskobjektet som undersøkes i listen
        boolean done = false;

        if (list.header == null) { // listen er tom
            list.header = presentTask; // nåværende Task blir første og siste objekt i TaskList
            list.trailer = presentTask; 
        } else { // listen er ikke tom
            examinatedTask = list.header; // begynner å undersøke første objekt i listen

            if (examinatedTask.priority > presentTask.priority) { // dersom første objekt i listen har lavere prioritet, sett inn Task først
                list.header = presentTask;
                presentTask.next = examinatedTask;
                examinatedTask.previous = presentTask;                
            } else {
                while(!done) {
                    if (examinatedTask.next != null && examinatedTask.next.priority > presentTask.priority) { // neste objekt i listen er ikke null og har lavere prioritet enn Task som skal inn
                        presentTask.next = examinatedTask.next; // setter presentTask sine next- og previouspekere på riktige Taskobjekter i listen
                        presentTask.previous = examinatedTask;
                        examinatedTask.next.previous = presentTask; // oppdaterer next- og previouspekere til de to Taskobjektene før og etter Task som settes inn
                        examinatedTask.next = presentTask;
                        done = true;
                    } else if (examinatedTask.next == null){ // neste objekt i listen er null
                        examinatedTask.next = presentTask;
                        presentTask.previous = examinatedTask;
                        list.trailer = presentTask;
                        done = true;
                    } 
                    examinatedTask = examinatedTask.next; // går videre i listen
                }
            }
        }
        list.numberOfTasks++;
    }


    /** Metoden removeTask() leter gjennom TaskList til Task med samme ID er funnet,
     * og fjerner dette Taskobjektet fra listen. Kan vurdere en mer effektiv søkemetode ved behov.
     */
    public boolean removeTask(int ID) {
    
        presentTask = list.header;
        Task prevTask = null;
        Task nextTask = null;
            
        if (presentTask == null) { // listen er tom
            return false;
        }
       
        for (int i = 0; i < list.numberOfTasks; i++) {
            if (presentTask.taskID == ID) { // Taskobjektet er funnet og skal nå fjernes
                if (presentTask == list.header) { // dersom dette er første objekt i listen
                    if (presentTask.next == null) { // dersom det kun er ett Taskobjekt i listen
                        list.header = null;
                    } else { // det er flere enn ett Taskobjekt i listen
                        list.header = presentTask.next;
                        list.header.previous = null;
                    }
                } else if (presentTask == list.trailer) { // dersom dette er siste objekt i listen
                    list.trailer = presentTask.previous;
                    list.trailer.next = null;
                } else { // objektet er verken først eller sist i listen
                    prevTask = presentTask.previous;
                    nextTask = presentTask.next;
                    prevTask.next = nextTask;
                    nextTask.previous = prevTask;
                }
                list.numberOfTasks--;
                return true;
            }
            presentTask = presentTask.next; // undersøker neste Taskobjekt i listen
        }
        return false; // fant ikke objektet i listen
    }


    /** Metoden printTaskList() løper gjennom listen og printer ut alle oppgaver,
     *  tilhørende prioritet og ID     
     * */
    public void printTaskList() {

        if (list.header == null) {
            System.out.println("Ingen oppgaver lagt til!");
        } else {
            Task myTask = list.header;
            System.out.println("\nDu har " + list.numberOfTasks + " oppgave(r) i listen:\n");
            for (int i = 0; i < list.numberOfTasks; i++) {
                System.out.println("Prioritet: " + myTask.priority + ". Task: " + myTask.taskContent + ". ID: " + myTask.taskID);
                myTask = myTask.next;
            }
        }
    }

} // Slutt på TaskController

class TaskList {

    Task header = null; // skal peke på første Task (node) i listen
    Task trailer = null; // skal peke på siste Task (node) i listen
    int numberOfTasks = 0; // antall objekter av klassen Task i listen, oppdateres i insertTask() og removeTask()

}


/** Objekter av klassen Task fungerer som noder i den dobbeltlinkede listen TaskList.
*  Hver Task peker på en tidligere Task (bortsett fra første Task i listen der previous peker på null,
*  og siste Task i listen der next peker på null).
*  Hver Task inneholder en unik ID, taskContent der selve oppgaven er lagret og en prioritet.
*/
class Task {

    Task next = null;
    Task previous = null;

    int taskID = 0;
    String taskContent = null;
    int priority = 0;
}
