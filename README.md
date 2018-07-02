# greenhouse


## Istruzioni d'uso
* Clonare il repository con il comando:
  git clone https://github.com/m-sirianni/greenhouse
* Importare la cartella "src" del repository in Eclipse (tra le librerie esterne, oltre a Paho MQTT e httpserver (resi disponibili sul dir in formato .jar), è necessario scaricare [json-simple](http://central.maven.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar), una libreria per il parsing di JSON).
* Compilare e avviare l'eseguibile.
* Aprendo la pagina web "localhost:10046" da browser sarà possibile trovare la pagina di configurazione e reset della piattaforma. Per avviare il programma caricare il file di configurazione "config.json", presente in "greenhau5". Dopo averlo selezionato, il sistema parserà automaticamente i dati e li invierà all'applicazione.
* Per utilizzare in maniera interattiva il software di gestione della serra, aprire l'interfaccia locale "index.html" presente in "greenhau5".

## Rapporto tecnico
* Il rapporto tecnico è nel file "relazione_reti2.pdf"
* In "uml.pdf" è presente l'approccio UML utilizzato per la progettazione dell'applicazione
