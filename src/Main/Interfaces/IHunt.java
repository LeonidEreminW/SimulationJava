package Main.Interfaces;

import Main.Coordinates;

public interface IHunt<Сoordinates> {
    void hunt(Coordinates preyCoordinates, int damage);
}
