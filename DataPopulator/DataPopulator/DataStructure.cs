namespace DataPopulator
{
    public class User
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string Username { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
        public int Vote {  get; set; }
        public List<Vote>? Votes { get; set; }
    }

    public class Vote
    {
        public int Id { get; set; }
        public int Option { get; set; }
        public int UserId { get; set; }
        public List<User>? Users { get; set; }
    }
}
