namespace Kronos {

    public class Probe {

        private Entity a, b, c;
        private Ray ray;

        public async void Update() {
            if (Physics.Collision(b, c)) {
                Debug.Log("Collision detected!");
            }

            if (Physics.Raycast(ray, a, out RaycastHit hit)) {
                Debug.Log("Hit " + hit.collider.name);
            }

            if (Physics.Boxcast(a, b, c)) {
                Debug.Log("Boxcast hit!");
            }
        }

    }

}