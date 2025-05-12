public abstract class AbstractEntity {
    private String img;
    private Coordinates position;
    public EntityType type;
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public Coordinates getPosition() {
        return position;
    }
    public void setPosition(Coordinates position) {
        this.position = position;
    }


}
