package dk.apaq.printing.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author michael
 */
public class PrinterManager implements PrinterListChangeNotifier {

    private final List<PrinterManagerPlugin> plugins = new ArrayList<PrinterManagerPlugin>();
    private final List<PrinterListChangeListener> listeners = new ArrayList<PrinterListChangeListener>();
    private final PluginListener pluginListener = new PluginListener();
    private DefaultPrinterDecisionMaker defaultPrinterDecisionMaker = new FirstInDefaultPrinterDecisionMaker();

    private class PluginListener implements PrinterListChangeListener {

        public void onPrinterListChange(PrinterEvent event) {
            for(PrinterListChangeListener listener : listeners) {
                listener.onPrinterListChange(event);
            }
        }
    }

    public void addPlugin(PrinterManagerPlugin plugin) {
        plugins.add(plugin);
        plugin.addListener(pluginListener);
    }

    public void removePlugin(PrinterManagerPlugin plugin) {
        plugins.remove(plugin);
        plugin.removeListener(pluginListener);
    }

    public int getPluginCount() {
        return plugins.size();
    }

    public PrinterManagerPlugin getPlugin(int index) {
        return plugins.get(index);
    }

    public List<Printer> getPrinters() {
        List<Printer> printerlist = new ArrayList<Printer>();
        for (PrinterManagerPlugin plugin : plugins) {
            for (Printer printer : plugin.getPrinters()) {
                printerlist.add(printer);
            }
        }
        return Collections.unmodifiableList(printerlist);
    }

    public Printer getDefaultPrinter() {
        return defaultPrinterDecisionMaker.getDefaultPrinter(this);
    }

    public void print(PrinterJob job) {
        for (PrinterManagerPlugin plugin : plugins) {
            for (Printer printer : plugin.getPrinters()) {
                String id = job.getPrinter().getId();
                String id2 = printer.getId();
                if (job.getPrinter().getId().equals(printer.getId())) {
                    plugin.print(job);
                    return;
                }
            }
        }
        throw new PrinterException("Printer specified in job is not maintained by this manager. [id=" + job.getPrinter().getId() + "]");
    }

    @Override
    public void addListener(PrinterListChangeListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(PrinterListChangeListener listener) {
        this.listeners.remove(listener);
    }

    public void setDefaultPrinterDecisionMaker(DefaultPrinterDecisionMaker defaultPrinterDecisionMaker) {
        this.defaultPrinterDecisionMaker = defaultPrinterDecisionMaker;
    }
    
    
}
