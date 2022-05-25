package GameLoader.client;

public interface ViewModel {
    Client getModelUser();
    void setElements(GuiElements fooElements);
    GeneralView createView();
}
